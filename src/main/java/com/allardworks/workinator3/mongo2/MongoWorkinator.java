package com.allardworks.workinator3.mongo2;

import com.allardworks.workinator3.commands.*;
import com.allardworks.workinator3.contracts.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoWriteException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.allardworks.workinator3.core.ConvertUtility.*;
import static com.allardworks.workinator3.mongo2.DocumentUtility.doc;

/**
 * Mongo implementation of the workinator.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MongoWorkinator implements Workinator {
    private final MongoDal dal;
    private final PartitionConfigurationCache configurationCache;
    private final AssignmentStrategy assignmentStrategy;

    /**
     * Create a partition.
     *
     * @param command
     * @throws PartitionExistsException
     */
    public void createPartition(final CreatePartitionCommand command) throws PartitionExistsException {
        val create = doc(
                // key
                "partitionKey", command.getPartitionKey(),

                // info
                "createDate", new Date(),

                // configuration
                "configuration", doc(
                        "maxIdleTimeSeconds", command.getMaxIdleTimeSeconds(),
                        "maxWorkerCount", command.getMaxWorkerCount()),

                // status
                "status", doc(
                        "assignmentCount", 0,
                        "hasWork", false,
                        "lastCheckedDate", toDate(MIN_DATE),
                        "dueDate", toDate(MIN_DATE),
                        "workerCount", 0,
                        "workers", new ArrayList<BasicDBObject>()));

        try {
            dal.getPartitionsCollection().insertOne(create);
        } catch (final MongoWriteException e) {
            if (e.getMessage().contains("E11000 duplicate key error collection")) {
                throw new PartitionExistsException(command.getPartitionKey());
            }
            throw e;
        }
    }

    @Override
    public void updateWorkerStatus(final UpdateWorkersStatusCommand workerStatus) {
        for (val status : workerStatus.getStatus()) {
            if (status.getCurrentAssignment() == null) {
                continue;
            }

            try {
                val updatePartition = doc("$set", doc(
                        "status.hasWork", status.isHasWork(),
                        "status.lastCheckedDate", new Date()));

                val find = doc("partitionKey", status.getCurrentAssignment().getPartitionKey());
                dal.getPartitionsCollection().updateOne(find, updatePartition);
            } catch (final Exception ex) {
                log.error("Error updating worker status.", ex);
            }
        }
    }

    @Override
    public List<PartitionInfo> getPartitions() {
        val result = new ArrayList<PartitionInfo>();
        val partitions = dal.getPartitionsCollection().find().iterator();
        partitions.forEachRemaining(doc -> {
            val workers = new ArrayList<WorkerInfo>();
            val workersSource = (List<Document>)doc.get("status.workers");
            workersSource.iterator().forEachRemaining(d -> workers.add(WorkerInfo.builder()
                    .assignee(d.getString("assignee"))
                    .createDate(toLocalDateTime(d.getDate("insertDate")))
                    .rule(d.getString("rule"))
                    .build()));

            result.add(PartitionInfo
                    .builder()
                    .partitionKey(doc.getString("partitionKey"))
                    .currentWorkerCount(doc.getInteger("status.workerCount"))
                    .hasMoreWork(doc.getBoolean("status.hasWork"))
                    .lastChecked(toLocalDateTime(doc.getDate("status.lastCheckedDate")))
                    .maxIdleTimeSeconds(doc.getInteger("configuration.maxIdleTimeSeconds"))
                    .maxWorkerCount(doc.getInteger("configuration.maxWorkerCount"))
                    .workers(workers)
                    .build());
        });
        return result;
    }

    /**
     * Retrieves and caches partition configuration objects.
     * They are cached for 5 minutes.
     * @param partitionKey
     * @return
     */
    @Override
    public PartitionConfiguration getPartitionConfiguration(final String partitionKey) {
        return configurationCache.getConfiguration(partitionKey);
    }

    /**
     * Get an assignment for the executor.
     *
     * @param status
     * @return
     */
    public Assignment getAssignment(@NonNull final WorkerStatus status) {
        return assignmentStrategy.getAssignment(status);
    }

    /**
     * Release the assignment.
     *
     * @param command
     */
    @Override
    public void releaseAssignment(@NonNull ReleaseAssignmentCommand command) {
        assignmentStrategy.releaseAssignment(command.getAssignment());
    }

    /**
     * Register a consumer.
     *
     * @param command
     * @return
     * @throws ConsumerExistsException
     */
    @Override
    public ConsumerRegistration registerConsumer(final RegisterConsumerCommand command) throws ConsumerExistsException {
        // todo: use receipt
        val consumer =
                doc("name", command.getId().getName(),
                        "connectDate", new Date(),
                        "maxWorkerCount", command.getMaxWorkerCount(),
                        "status", null);

        try {
            dal.getConsumersCollection().insertOne(consumer);
        } catch (final MongoWriteException e) {
            if (e.getMessage().contains("E11000 duplicate key error collection")) {
                throw new ConsumerExistsException(command.getId().getName());
            }
            throw e;
        }
        return new ConsumerRegistration(command.getId(), "");
    }

    private final ObjectMapper mapSerializer =
            new ObjectMapper()
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @Override
    public void updateConsumerStatus(final UpdateConsumerStatusCommand consumerStatus) {
        // TODO: receipt
        try {
            if (consumerStatus.getRegistration() == null) {
                // consumer isn't registered yet. nothing to save.
                return;
            }

            val map = mapSerializer.convertValue(consumerStatus, Map.class);
            val find = doc("name", consumerStatus.getRegistration().getConsumerId().getName());
            val update = doc("$set", doc("status", map));
            dal.getConsumersCollection().findOneAndUpdate(find, update);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Unregister the consumer.
     *
     * @param command
     */
    @Override
    public void unregisterConsumer(UnregisterConsumerCommand command) {
        // todo: use receipt
        val delete = doc("name", command.getRegistration().getConsumerId().getName());
        dal.getConsumersCollection().deleteOne(delete);
    }

    @Override
    public void close() {

    }
}
