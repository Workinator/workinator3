package com.allardworks.workinator3.mongo2;

import com.allardworks.workinator3.contracts.Assignment;
import com.allardworks.workinator3.contracts.ExecutorStatus;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.Date;

import static com.allardworks.workinator3.mongo2.DocumentUtility.doc;

@RequiredArgsConstructor
public class WhatsNextAssignmentStrategy implements AssignmentStrategy {
    private final MongoDal dal;

    /**
     * Get the a partition that is due to be processed, and doesn't have any
     * current workers.
     *
     * @param status
     * @return
     */
    private Assignment executeRule1(final ExecutorStatus status) {
        val filter = doc("workers", doc("$size", 0));

        val update = doc("$push",
                doc("workers",
                        doc("id", status.getExecutorId().getAssignee(),
                                "insertDate", new Date()),
                                "rule", "Rule 1"));

        val options = new FindOneAndUpdateOptions();
        options.returnDocument(ReturnDocument.AFTER);

        val result = dal.getPartitionsCollection().findOneAndUpdate(filter, update, options);
        if (result == null) {
            return null;
        }

        return new Assignment(status.getExecutorId(), result.getString("partitionKey"), "");
    }

    /**
     * If the executor is already busy, then let it keep doing what it's doing.
     * @param status
     * @return
     */
    private Assignment executeRule2(final ExecutorStatus status) {
        if (!status.isBusy()) {
            return status.getCurrentAssignment();
        }

        return null;
    }

    /**
     * Return a partition that is already being worked on.
     * @param status
     * @return
     */
    private Assignment executeRule3(final ExecutorStatus status) {
        val filter = doc("workers",
                doc("$size", doc("$gt", 0)),
                doc(""));

        val update = doc("$push",
                doc("workers",
                        doc("id", status.getExecutorId().getAssignee(),
                                "insertDate", new Date()),
                        "rule", "Rule 1"));

        val options = new FindOneAndUpdateOptions();
        options.returnDocument(ReturnDocument.AFTER);

        val result = dal.getPartitionsCollection().findOneAndUpdate(filter, update, options);
        if (result == null) {
            return null;
        }

        return new Assignment(status.getExecutorId(), result.getString("partitionKey"), "");
    }


    @Override
    public Assignment getAssignment(@NonNull final ExecutorStatus executor) {
        val r1 = executeRule1(executor);
        if (r1 != null) {
            return r1;
        }

        val r2 = executeRule2(executor);
        if (r2 != null) {
            return r2;
        }

        return null;
    }
}
