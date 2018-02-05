package com.allardworks.workinator3.mongo2;

import com.allardworks.workinator3.contracts.*;
import com.mongodb.BasicDBObject;
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

@RequiredArgsConstructor
public class MongoWorkinator implements Workinator {
    private final MongoDal dal;

    private MongoCollection<Document> getPartitions2Collection() {
        return dal.getDatabase().getCollection("Partitions2");
    }

    public void createPartition(final CreatePartitionCommand createCommand) {
        try {
            val create = new Document()
                    // key
                    .append("partitionKey", createCommand.getPartitionKey())

                    // configuration
                    .append("maxIdleTimeSeconds", createCommand.getMaxIdleTimeSeconds())
                    .append("hasWork", false)

                    // status
                    .append("dueDate", new SimpleDateFormat("d/m/yyyy").parse("1/1/2000"))
                    .append("workers", new ArrayList<BasicDBObject>());
            getPartitions2Collection().insertOne(create);
        } catch (ParseException e) {
            e.printStackTrace();
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

        // super fast
        //val update = new Document().append("$set", new Document().append("hasWork", true));

        val options = new FindOneAndUpdateOptions();
        options.returnDocument(ReturnDocument.AFTER);

        val result = getPartitions2Collection().findOneAndUpdate(filter, update, options);
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
        val result = getPartitions2Collection().findOneAndUpdate(findPartition, removeWorker, options);
    }

    @Override
    public ConsumerRegistration registerConsumer(ConsumerId id) throws ConsumerExistsException {
        return null;
    }

    @Override
    public void unregisterConsumer(ConsumerRegistration registration) {

    }
}
