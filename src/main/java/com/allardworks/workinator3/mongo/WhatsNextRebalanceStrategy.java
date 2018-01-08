package com.allardworks.workinator3.mongo;

import com.allardworks.workinator3.contracts.Assignment;
import com.allardworks.workinator3.contracts.ExecutorId;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.conversions.Bson;

import java.time.LocalDateTime;

import static com.allardworks.workinator3.core.ConvertUtility.toDate;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.ReturnDocument.AFTER;

public class WhatsNextRebalanceStrategy implements RebalanceStrategy {
    private final MongoDal dal;

    private final Bson rule1Filter;
    private final FindOneAndUpdateOptions updateOptions;

    public WhatsNextRebalanceStrategy(@NonNull final MongoDal dal) {
        this.dal = dal;
        rule1Filter = and(
                eq("partitionWorkerNumber", 0),
                eq("currentAssignee", null)
        );

        updateOptions = new FindOneAndUpdateOptions();
        updateOptions.returnDocument(AFTER);
        updateOptions.sort(new BasicDBObject("lastCheckEnd", 1));
    }

    @Override
    public Assignment getNextAssignment(final ExecutorId executorId) {
        val updateDoc = new BasicDBObject("$set",
                new BasicDBObject()
                        .append("currentAssignee", executorId.getAssignee())
                        .append("lastCheckStart", toDate(LocalDateTime.now()))
                        .append("lastCheckEnd", null));

        val match = dal.getWorkersCollection().findOneAndUpdate(rule1Filter, updateDoc, updateOptions);
        if (match == null) {
            return null;
        }

        return new Assignment(
                executorId,
                match.getString("partitionKey"),
                match.getInteger("partitionWorkerNumber"),
                "rule 1 - worker 1 due");
    }

    @Override
    public void releaseAssignment(Assignment assignment) {
        val queryDoc = and(
                eq("partitionKey", assignment.getPartitionKey()),
                eq("partitionWorkerNumber", assignment.getPartitionWorkerNumber()),
                eq("currentAssignee", assignment.getExecutorId().getAssignee()));

        val updateDoc = new BasicDBObject("$set",
                new BasicDBObject()
                        .append("currentAssignee", null)
                        .append("lastCheckEnd", toDate(LocalDateTime.now())));


        val result = dal.getWorkersCollection().findOneAndUpdate(queryDoc, updateDoc, updateOptions);
    }
}
