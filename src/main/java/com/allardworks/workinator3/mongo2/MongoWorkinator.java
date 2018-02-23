package com.allardworks.workinator3.mongo2;

import com.allardworks.workinator3.commands.CreatePartitionCommand;
import com.allardworks.workinator3.commands.RegisterConsumerCommand;
import com.allardworks.workinator3.commands.ReleaseAssignmentCommand;
import com.allardworks.workinator3.commands.UpdateWorkerStatusCommand;
import com.allardworks.workinator3.contracts.*;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoWriteException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.allardworks.workinator3.core.ConvertUtility.MIN_DATE;
import static com.allardworks.workinator3.core.ConvertUtility.toDate;
import static com.allardworks.workinator3.core.ConvertUtility.toLocalDateTime;
import static com.allardworks.workinator3.mongo2.DocumentUtility.doc;

/**
 * Mongo implementation of the workinator.
 */
@RequiredArgsConstructor
@Service
public class MongoWorkinator implements Workinator {
    private final MongoDal dal;
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

                // configuration
                "maxIdleTimeSeconds", command.getMaxIdleTimeSeconds(),
                "maxWorkerCount", command.getMaxWorkerCount(),

                // status
                "assignmentCount", 0,
                "hasWork", false,
                "lastCheckedDate", toDate(MIN_DATE),
                "dueDate", toDate(MIN_DATE),
                "workerCount", 0,
                "workers", new ArrayList<BasicDBObject>());

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
    public void updateStatus(final UpdateWorkerStatusCommand workerStatus) {
        if (workerStatus.getStatus().getCurrentAssignment() == null) {
            return;
        }

        val updatePartition = doc("$set", doc(
                "hasWork", workerStatus.getStatus().isHasWork(),
                "lastCheckedDate", new Date()));

        val find = doc("partitionKey", workerStatus.getStatus().getCurrentAssignment().getPartitionKey());

        dal.getPartitionsCollection().updateOne(find, updatePartition);
    }

    @Override
    public List<PartitionInfo> getPartitions() {
        val result = new ArrayList<PartitionInfo>();
        val partitions = dal.getPartitionsCollection().find().iterator();
        partitions.forEachRemaining(doc -> {
            val workers = new ArrayList<WorkerInfo>();
            val workersSource = (List<Document>)doc.get("workers");
            workersSource.iterator().forEachRemaining(d -> {
                workers.add(WorkerInfo.builder()
                        .id(d.getString("id"))
                        .createDate(toLocalDateTime(d.getDate("insertDate")))
                        .rule(d.getString("rule"))
                        .build());
            });

            result.add(PartitionInfo
                    .builder()
                    .partitionKey(doc.getString("partitionKey"))
                    .currentWorkerCount(doc.getInteger("workerCount"))
                    .hasMoreWork(doc.getBoolean("hasWork"))
                    .lastChecked(toLocalDateTime(doc.getDate("lastCheckedDate")))
                    .maxIdleTimeSeconds(doc.getInteger("maxIdleTimeSeconds"))
                    .maxWorkerCount(doc.getInteger("maxWorkerCount"))
                    .workers(workers)
                    .build());
        });
        return result;
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
        val consumer =
                doc("name", command.getId().getName(),
                        "connectDate", new Date(),
                        "maxWorkerCount", command.getMaxWorkerCount());

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

    /**
     * Unregister the consumer.
     *
     * @param registration
     */
    @Override
    public void unregisterConsumer(ConsumerRegistration registration) {

    }

    @Override
    public void close() throws Exception {

    }
}
