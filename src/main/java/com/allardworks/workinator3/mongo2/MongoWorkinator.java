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

                // status
                "hasWork", false,
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

    @Override
    public void updateStatus(final UpdateWorkerStatusCommand workerStatus) {
        val updatePartition = doc(
                "hasWork", workerStatus.getStatus().isHasWork(),
                "lastCheckedDate", new Date());

        val find = doc("partitionKey", workerStatus.getStatus().getCurrentAssignment().getPartitionKey());

        dal.getPartitionsCollection().updateOne(find, updatePartition);
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
