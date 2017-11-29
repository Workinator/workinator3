package com.allardworks.workinator3.mongo;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.core.ConvertUtility;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.Document;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;
import java.util.stream.IntStream;

import static com.allardworks.workinator3.core.ConvertUtility.toLocalDateTime;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public class MongoAdminRepository implements WorkinatorAdminRepository {
    private final MongoDal dal;

    @Override
    public void create(@NonNull final List<PartitionDto> partitions) throws PartitionExistsException {
        val documents =
                partitions
                        .stream()
                        .map(MongoAdminRepository::toBson)
                        .collect(toList());
        try {
            dal.getPartitionsCollection().insertMany(documents);
        } catch (final MongoWriteException e) {
            if (e.getMessage().contains("E11000 duplicate key error collection")) {
                throw new PartitionExistsException(null);
            }

            // otherwise...
            throw e;
        }

        createWorkers(partitions);
    }

    @Override
    public PartitionDto create(@NonNull final PartitionDto partition) throws PartitionExistsException {
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
        return getPartition(partition.getPartitionKey());
    }

    private void createWorkers(final List<PartitionDto> partitions) {
        val keys =
                partitions
                        .stream()
                        .map(PartitionDto::getPartitionKey)
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
                    .range(0, partition.getMaxWorkerCount())
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

    @Override
    public PartitionDto update(final PartitionDto partition) {
        throw new NotImplementedException();
    }

    @Override
    public PartitionDto delete(PartitionDto partition) {
        throw new NotImplementedException();
    }

    public PartitionDto getPartition(@NonNull final String partitionKey) {
        val doc = dal.getPartitionsCollection().find(eq("partitionKey", partitionKey)).first();
        return toPartitionDto(doc);
    }

    public static Document toBson(final PartitionDto partition) {
        val doc = new Document();
        doc.put("partitionKey", partition.getPartitionKey());

        doc.put("maxIdleSeconds", partition.getMaxIdleTimeSeconds());
        doc.put("maxWorkerCount", partition.getMaxWorkerCount());
        doc.put("hasMoreWork", partition.isHasMoreWork());
        doc.put("workCount", partition.getWorkCount());
        doc.put("lastCheckStart", ConvertUtility.toDate(partition.getLastCheckStart()));
        doc.put("lastCheckEnd", ConvertUtility.toDate(partition.getLastCheckEnd()));
        doc.put("lastWork", ConvertUtility.toDate(partition.getLastWork()));
        return doc;
    }

    public static PartitionDto toPartitionDto(final Document document) {
        val partition = new PartitionDto();
        partition.setPartitionKey(document.getString("partitionKey"));
        partition.setHasMoreWork(document.getBoolean("hasMoreWork"));
        partition.setMaxIdleTimeSeconds(document.getInteger("maxIdleSeconds"));
        partition.setWorkCount(document.getLong("workCount"));
        partition.setMaxWorkerCount(document.getInteger("maxWorkerCount"));
        partition.setLastWork(toLocalDateTime(document.getDate("lastWork")));
        partition.setLastCheckStart(toLocalDateTime(document.getDate("lastCheckStart")));
        partition.setLastCheckEnd(toLocalDateTime(document.getDate("lastCheckEnd")));
        return partition;
    }
}
