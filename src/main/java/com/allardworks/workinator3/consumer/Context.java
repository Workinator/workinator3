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

@RequiredArgsConstructor
public class Context implements WorkerContext {
    @NonNull private final LocalTime startDate = LocalTime.now();
    @NonNull private final Function<Context, Boolean> canContinue;
    @NonNull private final Assignment assignment;
    @NonNull private final ServiceStatus executorStatus;

    public boolean getHasMoreWork() {
        return assignment.getPartition().isHasMoreWork();
    }

    public void setHasMoreWork(boolean hasMoreWork) {
        assignment.getPartition().setMoreWork(hasMoreWork);
    }

    public WorkerContext didWork() {
        didWork(1);
        return this;
    }

    public WorkerContext didWork(final int workCount) {
        assignment.getPartition().didWork(workCount);
        return this;
    }

    public Duration getElapsed() {
        return Duration.between(LocalTime.now(), startDate);
    }

    public Status getExecutorStatus() {
        return executorStatus.getStatus();
    }

    @Override
    public boolean canContinue() {
        return canContinue.apply(this);
    }

    @Override
    public Assignment getAssignment() {
        return assignment;
    }
}
