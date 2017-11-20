package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.Assignment;
import com.allardworks.workinator3.contracts.WorkerContext;
import com.allardworks.workinator3.core.EventHandlers;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class Context implements WorkerContext {
    private final LocalTime startDate = LocalTime.now();
    private final Function<Context, Boolean> canContinue;
    private final Assignment assignment;
    private final EventHandlers stopHandlers;

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

    @Override
    public void onStopping(Runnable eventHandler) {
        stopHandlers.add(eventHandler);
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
