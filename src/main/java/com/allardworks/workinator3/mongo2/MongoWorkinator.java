package com.allardworks.workinator3.mongo2;

import com.allardworks.workinator3.commands.CreatePartitionCommand;
import com.allardworks.workinator3.commands.RegisterConsumerCommand;
import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.core.ConvertUtility;
import com.mongodb.BasicDBObject;
import com.mongodb.InsertOptions;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.allardworks.workinator3.core.ConvertUtility.toDate;

@RequiredArgsConstructor
public class MongoWorkinator implements Workinator {
    private final MongoDal dal;

    public void createPartition(final CreatePartitionCommand command) throws PartitionExistsException {
        try {
            val create = new Document()
                    // key
                    .append("partitionKey", command.getPartitionKey())

                    // configuration
                    .append("maxIdleTimeSeconds", command.getMaxIdleTimeSeconds())
                    .append("hasWork", false)

                    // status
                    .append("dueDate", toDate(ConvertUtility.MinDate))
                    .append("workers", new ArrayList<BasicDBObject>());
            dal.getPartitionsCollection().insertOne(create);
        } catch (final MongoWriteException e) {
            if (e.getMessage().contains("E11000 duplicate key error collection")) {
                throw new PartitionExistsException(command.getPartitionKey());
            }
            throw e;
        }
    }

    private Assignment executeRule1(@NonNull final ExecutorStatus status) {
        val filter = new Document()
                //.append("workers", new Document().append("$size", 0))
                .append("dueDate", new Document().append("$lt", new Date()));


        val update = new Document()
                .append("$push",
                        new Document().append("workers",
                                new Document()
                                        .append("id", status.getExecutorId().getAssignee())
                                        .append("insertDate", new Date())
                                        .append("rule", "Rule 1")));

        val options = new FindOneAndUpdateOptions();
        options.returnDocument(ReturnDocument.AFTER);

        val result = dal.getPartitionsCollection().findOneAndUpdate(filter, update, options);
        if (result == null) {
            return null;
        }
        return new Assignment(status.getExecutorId(), result.getString("partitionKey"), "");
    }

    public Assignment getAssignment(@NonNull ExecutorStatus status) {
        val assignment = executeRule1(status);
        if (assignment == null) {
            return null;
        }

        return assignment;
    }

    public void releaseAssignment(@NonNull Assignment assignment) {
        val findPartition = new Document().append("partitionKey", assignment.getPartitionKey());
        val removeWorker = new Document().append("$pull",
                new Document().append("workers",
                        new Document().append("id", assignment.getExecutorId().getAssignee())));

        val options = new FindOneAndUpdateOptions();
        options.projection(new Document().append("_id", 1));
        val result = dal.getPartitionsCollection().findOneAndUpdate(findPartition, removeWorker, options);
    }

    @Override
    public ConsumerRegistration registerConsumer(final RegisterConsumerCommand command) throws ConsumerExistsException {
        val consumer = new Document()
                .append("name", command.getId().getName())
                .append("connectDate", new Date())
                .append("maxWorkerCount", command.getMaxWorkerCount());

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

    @Override
    public void unregisterConsumer(ConsumerRegistration registration) {

    }

    @Override
    public void close() throws Exception {

    }
}
