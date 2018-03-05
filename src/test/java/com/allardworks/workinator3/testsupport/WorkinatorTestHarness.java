package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.WorkinatorTester;
import com.allardworks.workinator3.commands.CreatePartitionCommand;
import com.allardworks.workinator3.commands.ReleaseAssignmentCommand;
import com.allardworks.workinator3.contracts.*;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by jaya on 3/4/18.
 * k?
 */
@RequiredArgsConstructor
public class WorkinatorTestHarness implements AutoCloseable {
    private final WorkinatorTester tester;
    private final Map<String, WorkerStatus> workers = new HashMap<>();
    private final Map<String, Assignment> assignments = new HashMap<>();

    private WorkerStatus createWorkerStatus(final String consumerId) {
        return new WorkerStatus(new WorkerId(new ConsumerRegistration(new ConsumerId(consumerId), ""), 1));
    }

    @Override
    public void close() throws Exception {
        tester.close();
    }

    public WorkinatorTestHarness createPartition(final String partitionKey) throws Exception {
        tester
                .getWorkinator()
                .createPartition(
                        CreatePartitionCommand
                                .builder()
                                .partitionKey(partitionKey)
                                .build());
        return this;
    }

    public WorkinatorTestHarness createWorker(final String workerName) {
        val worker = createWorkerStatus(workerName);
        workers.put(workerName, worker);
        return this;
    }

    private Assignment getAssignment(final String workerName) {
        val worker = workers.get(workerName);
        val assignment = tester.getWorkinator().getAssignment(worker);
        worker.setCurrentAssignment(assignment);
        return assignment;
    }

    public WorkinatorTestHarness assertGetAssignment(final String workerName, final String expectedPartitionKey, final String expectedRule) {
        val assignment = getAssignment(workerName);
        assertEquals(expectedPartitionKey, assignment.getPartitionKey());
        assertEquals(expectedRule, assignment.getRuleName());
        assignments.put(workerName, assignment);
        return this;
    }

    public WorkinatorTestHarness assertNullAssignment(final String workerName) {
        val assignment = getAssignment(workerName);
        assertNull(assignment);
        return this;
    }

    public WorkinatorTestHarness releaseAssignment(final String workerName) {
        val assignment = assignments.get(workerName);
        tester.getWorkinator().releaseAssignment(new ReleaseAssignmentCommand(assignment));
        assignments.remove(workerName);
        return this;
    }

    public WorkinatorTestHarness setWorkerHasWork(final String workerName) {
        val worker = workers.get(workerName);
        worker.setHasWork(true);
        return this;
    }

    public WorkinatorTestHarness setWorkerDoesntHaveWork(final String workerName) {
        val worker = workers.get(workerName);
        worker.setHasWork(false);
        return this;
    }
}
