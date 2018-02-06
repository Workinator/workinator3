package com.allardworks.workinator3.mongo2;

import com.allardworks.workinator3.contracts.Assignment;
import com.allardworks.workinator3.contracts.ExecutorStatus;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.Document;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import static com.allardworks.workinator3.mongo2.DocumentUtility.doc;
import static com.mongodb.client.model.ReturnDocument.AFTER;
import static java.lang.System.out;

@RequiredArgsConstructor
public class WhatsNextAssignmentStrategy implements AssignmentStrategy {

    private final FindOneAndUpdateOptions updateOptions = new FindOneAndUpdateOptions()
            .returnDocument(AFTER)
            .sort(doc("lastCheckedDate", 1));

    private final MongoDal dal;

    @Override
    public Assignment getAssignment(@NonNull final ExecutorStatus status) {
        return new StrategyRunner(this, status).getAssignment();
    }

    @Override
    public void releaseAssignment(Assignment assignment) {
        // TODO: due date
        val findPartition = doc("partitionKey", assignment.getPartitionKey());
        val removeWorker =
                doc("$pull",
                        doc("workers",
                                doc("id", assignment.getExecutorId().getAssignee())),
                        "$inc", doc("workerCount", -1),
                        "$set", doc("lastChecked", new Date()));
        val options = new FindOneAndUpdateOptions().projection(new Document().append("_id", 1));
        val result = dal.getPartitionsCollection().findOneAndUpdate(findPartition, removeWorker, options);
        out.println(result);
    }

    @RequiredArgsConstructor
    private static class StrategyRunner {
        /**
         * The list of methods to execute to determine the assignment.
         * First one wins.
         */
        private final List<Supplier<Assignment>> rules = Arrays.asList(
                this::due,
                this::ifBusyKeepGoing,
                this::alreadyBeingWorkedOn,
                this::anyPartitionWithoutWorkers);

        private final WhatsNextAssignmentStrategy strategy;
        private final ExecutorStatus status;

        /**
         * Creates the update document. All rules need this.
         *
         * @param ruleName
         * @return
         */
        private Document createUpdateDocument(final String ruleName) {
            return doc("$push",
                    doc("workers",
                            doc("id", status.getExecutorId().getAssignee(),
                                    "insertDate", new Date(),
                                    "rule", "Rule 1")),
                    "$inc", doc("workerCount", 1),
                    "$set", doc("lastChecked", new Date()));
        }

        private Assignment toAssignment(final Document partition, final ExecutorStatus status, final String ruleName) {
            if (partition == null) {
                return null;
            }

            return new Assignment(status.getExecutorId(), partition.getString("partitionKey"), "", ruleName);
        }

        /**
         * Get the a partition that is due to be processed, and doesn't have any
         * current workers.
         *
         * @return
         */
        private Assignment due() {
            val where = doc("workerCount", 0, "dueDate", doc("$lt", new Date()));
            val update = createUpdateDocument("Rule 1");
            return toAssignment(strategy.dal.getPartitionsCollection().findOneAndUpdate(where, update, strategy.updateOptions), status, "Rule 1");
        }

        /**
         * If the executor is already busy, then let it keep doing what it's doing.
         *
         * @return
         */
        private Assignment ifBusyKeepGoing() {
            if (!status.isBusy()) {
                return status.getCurrentAssignment();
            }

            return null;
        }

        /**
         * Return a partition that is already being worked on.
         *
         * @return
         */
        private Assignment alreadyBeingWorkedOn() {
            val updateOptions = new FindOneAndUpdateOptions()
                    .returnDocument(AFTER)
                    .sort(doc("lastCheckedDate", 1, "workerCount", 1));

            val where = doc("hasWork", true);
            val update = createUpdateDocument("Rule 3");
            return toAssignment(strategy.dal.getPartitionsCollection().findOneAndUpdate(where, update, updateOptions), status, "Rule 3");
        }

        /**
         * Find any partition that doesn't have a worker.
         */
        private Assignment anyPartitionWithoutWorkers() {
            val where = doc("workerCount", 0);
            val update = createUpdateDocument("Rule 4");
            return toAssignment(strategy.dal.getPartitionsCollection().findOneAndUpdate(where, update, strategy.updateOptions), status, "Rule 4");
        }

        public Assignment getAssignment() {
            for (val rule : rules) {
                val assignment = rule.get();
                if (assignment != null) {
                    return assignment;
                }
            }

            return null;
        }
    }
}
