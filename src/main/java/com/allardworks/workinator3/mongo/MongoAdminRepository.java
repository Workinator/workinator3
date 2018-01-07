package com.allardworks.workinator3.mongo;

import com.allardworks.workinator3.contracts.*;
import com.mongodb.MongoWriteException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.Document;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

import static com.allardworks.workinator3.core.ConvertUtility.toDate;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class MongoAdminRepository implements WorkinatorAdminRepository {
    private final MongoDal dal;

    /**
     * Converts the partition dao to a bson document.
     * Includes only properties that are set.
     * @param partition
     * @return
     */
    private static Document toBson(final PartitionDao partition) {
        val doc = new Document();
        doc.put("partitionKey", partition.getPartitionKey());
        partition.getMaxWorkerCount().ifPresent(v -> doc.put("maxWorkerCount", v.getValue()));
        partition.getHasMoreWork().ifPresent(v -> doc.put("hasMoreWork", v.getValue()));
        partition.getLastCheckEnd().ifPresent(v -> doc.put("lastCheckEnd", toDate((LocalDateTime) v.getValue())));
        partition.getLastCheckStart().ifPresent(v -> doc.put("lastCheckStart", toDate((LocalDateTime) v.getValue())));
        partition.getLastWork().ifPresent(v -> doc.put("lastWork", toDate((LocalDateTime) v.getValue())));
        partition.getMaxIdleTimeSeconds().ifPresent(v -> doc.put("maxIdleTimeSeconds", v.getValue()));
        partition.getWorkCount().ifPresent(v -> doc.put("workCount", v.getValue()));
        return doc;
    }

    /**
     * Creates the workers for the partitions.
     * This is additive only; if the worker count decreases, the orphan rows
     * aren't deleted.
     * @param partitions
     */
    private void createWorkers(final List<PartitionDao> partitions) {
        val keys =
                partitions
                        .stream()
                        .map(PartitionDao::getPartitionKey)
                        .collect(toList());

        val existing2 =
                dal
                        .getWorkersCollection()
                        .find(in("partitionKey", keys))
                        .projection(fields(include("partitionKey", "partitionWorkerNumber"), excludeId()))
                        .into(new ArrayList<>())
                        .stream()
                        .map(p -> p.getString("partitionKey") + "." + p.get("partitionWorkerNumber"))
                        .collect(toSet());


        val docsToCreate = new ArrayList<Document>();
        for (val partition : partitions) {
            docsToCreate.addAll(IntStream
                    .range(0, partition.getMaxWorkerCount().getValue())
                    .filter(i -> !existing2.contains(partition.getPartitionKey() + "." + i))
                    .mapToObj(partitionWorkerNumber -> {
                        val doc = new Document();
                        doc.put("partitionKey", partition.getPartitionKey());
                        doc.put("partitionWorkerNumber", partitionWorkerNumber);
                        doc.put("currentAssignee", null);
                        return doc;
                    })
                    .collect(toList()));
        }
        dal.getWorkersCollection().insertMany(docsToCreate);
    }

    /**
     * Create coordinator partitions.
     * @param partitions
     * @throws PartitionExistsException
     */
    @Override
    public void createPartitions(List<PartitionDao> partitions) throws PartitionExistsException {
        // TODO: insert many
        for (val p : partitions) {
            createPartition(p);
        }
    }

    /**
     * Create a partition.
     * @param partition
     * @throws PartitionExistsException
     */
    @Override
    public void createPartition(final PartitionDao partition) throws PartitionExistsException {
        val doc = toBson(partition);
        try {
            dal.getPartitionsCollection().insertOne(doc);
        } catch (final MongoWriteException e) {
            if (e.getMessage().contains("E11000 duplicate key error collection")) {
                throw new PartitionExistsException(partition.getPartitionKey());
            }

            // otherwise...
            throw e;
        }

        createWorkers(singletonList(partition));
    }

    /**
     * Update the partition.
     * @param partition
     */
    @Override
    public void updatePartition(PartitionDao partition) {

    }
}
