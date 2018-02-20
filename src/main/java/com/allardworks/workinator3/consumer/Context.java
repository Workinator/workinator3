package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.Assignment;
import com.allardworks.workinator3.contracts.WorkerContext;
import com.allardworks.workinator3.contracts.WorkerStatus;
import com.allardworks.workinator3.core.ServiceStatus;
import com.allardworks.workinator3.core.Status;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Runtime context for a worker. This is passed to the worker.
 * The worker uses it to communicate with the coordinator.
 */
@RequiredArgsConstructor
public class Context implements WorkerContext {
    @NonNull private final Assignment assignment;
    @NonNull private final WorkerStatus workerStatus;
    @NonNull private final Supplier<ServiceStatus> executorStatus;
    @NonNull private final Function<Context, Boolean> canContinue;

    @NonNull private final LocalTime startDate = LocalTime.now();

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
        workerStatus.setHasWork(hasMoreWork);
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
        return executorStatus.get().getStatus();
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
