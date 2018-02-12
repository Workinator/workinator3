package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.core.ServiceStatus;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.function.Function;

/**
 * Manages workers runners.
 * The executors need runners. Runners are based on assignments.
 * This creates/closes runners as assignments changes.
 * If the assignment doesn't change, then it will return the existing runner.
 * If there isn't an assignment, then it returns a null.
 */
@RequiredArgsConstructor
class WorkerRunnerProvider implements AutoCloseable {
    private final Function<Context, Boolean> canContinue;
    private final AsyncWorkerFactory workerFactory;
    private final Workinator workinator;
    private final WorkerStatus workerStatus;
    private final ServiceStatus serviceStatus;
    private WorkerRunner current;

    /**
     * Terminate the existing runner, if there is one.
     */
    private void closeCurrent() {
        if (current == null) {
            return;
        }
        current.close();
        current = null;
    }

    /**
     * Creates a new WorkerRunner.
     *
     * @param newAssignment
     * @return
     */
    private WorkerRunner createWorkerRunner(final Assignment newAssignment) {
        val worker = workerFactory.createWorker(newAssignment);
        val context = new Context(canContinue, newAssignment, serviceStatus);
        return new WorkerRunner(workinator, workerStatus, worker, context);
    }

    /**
     * Returns the current assignment.
     *
     * @return
     */
    public Assignment getCurrentAssignment() {
        val c = current;
        if (c == null) {
            return null;
        }

        return c.getStatus().getCurrentAssignment();
    }

    /**
     * Returns the current run context.
     *
     * @return
     */
    public WorkerContext getCurrentContext() {
        val c = current;
        if (c == null) {
            return null;
        }

        return c.getContext();
    }

    /**
     * Creates a new WorkerRunner, or returns the existing one.
     * - if there isn't an assignment for this worker, then returns null.
     * - if there's a new assignment, closes the old runner and returns a new one.
     * - if the assignment doesn't change, then returns the current runner.
     *
     * @return
     */
    public WorkerRunner lookupRunner() {
        val newAssignment = workinator.getAssignment(workerStatus);

        // no assignment. nothing to do.
        if (newAssignment == null) {
            closeCurrent();
            return null;
        }

        // new assignment.
        if (
                current == null
                        || current.getStatus().getCurrentAssignment() == null
                        || !current.getStatus().getCurrentAssignment().equals(newAssignment)) {
            closeCurrent();
            current = createWorkerRunner(newAssignment);
        }

        return current;
    }

    /**
     * Terminate the current worker runner.
     *
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        closeCurrent();
    }
}