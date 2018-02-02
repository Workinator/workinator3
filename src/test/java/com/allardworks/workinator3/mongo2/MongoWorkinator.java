package com.allardworks.workinator3.mongo2;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.mongo.MongoDal;
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
import java.time.Instant;
import java.util.Date;

@RequiredArgsConstructor
public class MongoWorkinator {
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
                    .append("workerCount", 0);
            getPartitions2Collection().insertOne(create);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private Document executeRule1(@NonNull final ExecutorStatus status) {
        val filter = new Document()
                .append("workerCount", 0)
                .append("dueDate", new Document().append("$lt", new Date()));


        val update = new Document()
                .append("$inc", new Document().append("workerCount", 1));

        val options = new FindOneAndUpdateOptions();
        options.returnDocument(ReturnDocument.AFTER);

        return getPartitions2Collection().findOneAndUpdate(filter, update, options);

    }

    public Assignment getAssignment(@NonNull ExecutorStatus status) {
        val assignment = executeRule1(status);
        if (assignment == null) {
            return null;
        }

        return new Assignment(status.getExecutorId(), assignment.getString("partitionKey"), 0, "Rule1");

    }

    public void releaseAssignment(@NonNull Assignment assignment) {
    }
}
