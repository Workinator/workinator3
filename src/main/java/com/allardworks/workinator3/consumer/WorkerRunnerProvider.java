package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.core.ServiceStatus;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.function.Function;

@RequiredArgsConstructor
class WorkerRunnerProvider implements AutoCloseable {
    private final Function<Context, Boolean> canContinue;
    private final AsyncWorkerFactory workerFactory;
    private final Workinator repo;
    private final ExecutorStatus executorId;
    private final ServiceStatus serviceStatus;
    private WorkerRunner current;

    private void closeCurrent() {
        if (current == null) {
            return;
        }
        current.close();
        current = null;
    }

    private WorkerRunner createWorkerRunner(final Assignment newAssignment) {
        val worker = workerFactory.createWorker(newAssignment);
        val context = new Context(canContinue, newAssignment, serviceStatus);
        return new WorkerRunner(repo, newAssignment, worker, context);
    }

    public Assignment getCurrentAssignment() {
        val c = current;
        if (c == null) {
            return null;
        }

        return c.getAssignment();
    }

    public WorkerContext getCurrentContext() {
        val c = current;
        if (c == null) {
            return null;
        }

        return c.getContext();
    }

    public WorkerRunner lookupRunner() {
        val newAssignment = repo.getAssignment(executorId);

        // no assignment. nothing to do.
        if (newAssignment == null) {
            closeCurrent();
            return null;
        }

        // new assignment.
        if (current == null || !current.getAssignment().equals(newAssignment)) {
            closeCurrent();
            current = createWorkerRunner(newAssignment);
        }

        return current;
    }

    @Override
    public void close() throws Exception {
        closeCurrent();
    }
}