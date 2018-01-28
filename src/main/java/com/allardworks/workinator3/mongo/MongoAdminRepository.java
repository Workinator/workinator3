package com.allardworks.workinator3.mongo;

import com.allardworks.workinator3.contracts.*;
import com.mongodb.MongoWriteException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.BsonDocument;
import org.bson.Document;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

import static com.allardworks.workinator3.core.ConvertUtility.toDate;
import static com.allardworks.workinator3.core.ConvertUtility.toLocalDateTime;
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

    private static PartitionDao toPartition(final Document document) {
        val dao = new PartitionDao();
        dao.setPartitionKey(document.getString("partitionKey"));
        dao.getMaxWorkerCount().setValue(document.getInteger("maxWorkerCount"));
        dao.getHasMoreWork().setValue(document.getBoolean("hasMoreWork"));
        dao.getLastCheckEnd().setValue(toLocalDateTime(document.getDate("lastCheckEnd")));
        dao.getLastCheckStart().setValue(toLocalDateTime(document.getDate("lastCheckStart")));
        dao.getLastWork().setValue(toLocalDateTime(document.getDate("lastWork")));
        dao.getMaxIdleTimeSeconds().setValue(document.getInteger("maxIdleTimeSeconds"));
        dao.getWorkCount().setValue(document.getLong("workCount"));
        return dao;
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
                        .projection(fields(include("partitionKey", "workerNumber"), excludeId()))
                        .into(new ArrayList<>())
                        .stream()
                        .map(p -> p.getString("partitionKey") + "." + p.get("workerNumber"))
                        .collect(toSet());


        val docsToCreate = new ArrayList<Document>();
        for (val partition : partitions) {
            docsToCreate.addAll(IntStream
                    .range(0, partition.getMaxWorkerCount().getValue())
                    .filter(i -> !existing2.contains(partition.getPartitionKey() + "." + i))
                    .mapToObj(workerNumber -> {
                        val doc = new Document();
                        doc.put("partitionKey", partition.getPartitionKey());
                        doc.put("workerNumber", workerNumber);
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
    public PartitionDao createPartition(final PartitionDao partition) throws PartitionExistsException {
        val doc = toBson(partition);
        try {
            dal.getPartitionsCollection().insertOne(doc);
        } catch (final MongoWriteException e) {
            if (e.getMessage().contains("E11000 duplicate key error collection")) {
                throw new PartitionExistsException(partition.getPartitionKey());
            }

            throw e;
        }

        createWorkers(singletonList(partition));
        try {
            return getPartition(partition.getPartitionKey());
        } catch (PartitionDoesntExistException e) {
            e.printStackTrace();
            // TODO log. this shouldn't happen. we just created it, so why wouldn't it exist/
            return null;
        }
    }

    @Override
    public List<PartitionDao> getPartitions() {
        val result = new ArrayList<PartitionDao>();
        for (val doc :  dal.getPartitionsCollection().find()) {
            result.add(toPartition(doc));
        }
        return result;
    }

    @Override
    public PartitionDao getPartition(@NonNull final String partitionKey) throws PartitionDoesntExistException {
        val filter = new Document();
        filter.put("partitionKey", partitionKey);
        val partition = dal.getPartitionsCollection().find(filter).first();
        if (partition == null) {
            throw new PartitionDoesntExistException(partitionKey);
        }
        return toPartition(partition);
    }

    /**
     * Update the partition.
     * @param partition
     */
    @Override
    public void updatePartition(PartitionDao partition) {

    }
}
