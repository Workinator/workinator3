package com.allardworks.workinator3.mongo2;

import com.allardworks.workinator3.commands.CreatePartitionCommand;
import com.allardworks.workinator3.commands.RegisterConsumerCommand;
import com.allardworks.workinator3.contracts.*;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;

import static com.allardworks.workinator3.core.ConvertUtility.MinDate;
import static com.allardworks.workinator3.core.ConvertUtility.toDate;
import static com.allardworks.workinator3.mongo2.DocumentUtility.doc;

/**
 * Mongo implementation of the workinator.
 */
@RequiredArgsConstructor
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
                "maxIdleTimeSeconds", command.getPartitionKey(),
                "hasWork", false,
                // status
                "lastCheckedDate", toDate(MinDate),
                "dueDate", toDate(MinDate),
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

    /**
     * Get an assignment for the executor.
     *
     * @param status
     * @return
     */
    public Assignment getAssignment(@NonNull ExecutorStatus status) {
        return assignmentStrategy.getAssignment(status);
    }

    /**
     * Release the assignment.
     *
     * @param assignment
     */
    public void releaseAssignment(@NonNull Assignment assignment) {
        assignmentStrategy.releaseAssignment(assignment);
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
