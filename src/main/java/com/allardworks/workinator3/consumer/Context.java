package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.Assignment;
import com.allardworks.workinator3.contracts.WorkerContext;
import com.allardworks.workinator3.core.ServiceStatus;
import com.allardworks.workinator3.core.Status;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Function;

/**
 * Runtime context for a worker. This is passed to the worker.
 * The worker uses it to communicate with the coordinator.
 */
@RequiredArgsConstructor
public class Context implements WorkerContext {
    @NonNull private final LocalTime startDate = LocalTime.now();
    @NonNull private final Function<Context, Boolean> canContinue;
    @NonNull private final Assignment assignment;
    @NonNull private final ServiceStatus executorStatus;

    public boolean getHasMoreWork() {
        return false;
        // TODO
        //return assignment.isHasMoreWork();
    }

    /**
     * The worker reports to the coordinator that there is more work to do.
     * @param hasMoreWork
     */
    public void setHasMoreWork(boolean hasMoreWork) {
        // TODO
        //assignment.getPartition().setMoreWork(hasMoreWork);
    }

    /**
     * The worker reports that it completed one unit of work.
     * @return
     */
    public WorkerContext didWork() {
        didWork(1);
        return this;
    }

    /**
     * The worker reports that it did 0 or more units of work.
     * @param workCount
     * @return
     */
    public WorkerContext didWork(final int workCount) {
        // TODO
        //assignment.getPartition().didWork(workCount);
        return this;
    }

    /**
     * Gets the length of time that the context has been alive.
     * @return
     */
    public Duration getElapsed() {
        return Duration.between(LocalTime.now(), startDate);
    }

    /**
     * Gets the executors current status.
     * @return
     */
    public Status getExecutorStatus() {
        return executorStatus.getStatus();
    }

    /**
     * Returns true if the worker may continue working.
     * Returns false when the worker needs to stop.
     * @return
     */
    @Override
    public boolean canContinue() {
        return canContinue.apply(this);
    }

    /**
     * Returns the worker's assignment.
     * @return
     */
    @Override
    public Assignment getAssignment() {
        return assignment;
    }
}
