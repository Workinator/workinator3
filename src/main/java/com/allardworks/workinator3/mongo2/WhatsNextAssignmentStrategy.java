package com.allardworks.workinator3.mongo2;

import com.allardworks.workinator3.contracts.Assignment;
import com.allardworks.workinator3.contracts.ExecutorStatus;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.Document;

import java.util.Date;

@RequiredArgsConstructor
public class WhatsNextAssignmentStrategy implements AssignmentStrategy {
    private final MongoDal dal;

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

    @Override
    public Assignment getAssignment(ExecutorStatus executor) {
        return null;
    }
}
