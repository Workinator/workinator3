package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.Assignment;
import com.allardworks.workinator3.contracts.WorkerContext;
import com.allardworks.workinator3.core.ServiceStatus;
import com.allardworks.workinator3.core.Status;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Consumer;
import java.util.function.Function;

@RequiredArgsConstructor
public class Context implements WorkerContext {
    @NonNull private final LocalTime startDate = LocalTime.now();
    @NonNull private final Function<Context, Boolean> canContinue;
    @NonNull private final Assignment assignment;
    @NonNull private final ServiceStatus executorStatus;

    private boolean hasMoreWork = true;

    public boolean getHasMoreWork() {
        return hasMoreWork;
    }

    public void setHasMoreWork(boolean hasMoreWork) {
        this.hasMoreWork = hasMoreWork;
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
