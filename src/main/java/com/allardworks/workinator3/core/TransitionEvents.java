package com.allardworks.workinator3.core;

import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

import static com.allardworks.workinator3.core.Status.*;
import static com.allardworks.workinator3.core.TransitionStage.AfterTransition;
import static com.allardworks.workinator3.core.TransitionStage.BeforeTransition;

@RequiredArgsConstructor
public class TransitionEvents {
    private final ServiceStatus status;

    public void onPreStarting(final Consumer<Transition> transition) {
        status.onTransition(t -> {
            if (t.getStage().equals(BeforeTransition) && t.getAfter().equals(Starting)) {
                transition.accept(t);
            }
        });
    }

    public void onPostStarting(final Consumer<Transition> transition) {
        status.onTransition(t -> {
            if (t.getStage().equals(AfterTransition) && t.getAfter().equals(Starting)) {
                transition.accept(t);
            }
        });
    }

    public void onPostStopping(final Consumer<Transition> transition) {
        status.onTransition(t -> {
            if (t.getStage().equals(AfterTransition) && t.getAfter().equals(Stopping)) {
                transition.accept(t);
            }
        });
    }

    public void onPostStarted(final Consumer<Transition> transition) {
        status.onTransition(t -> {
            if (t.getStage().equals(AfterTransition) && t.getAfter().equals(Started)) {
                transition.accept(t);
            }
        });
    }

    public void onPostStopped(final Consumer<Transition> transition) {
        status.onTransition(t -> {
            if (t.getStage().equals(AfterTransition) && t.getAfter().equals(Stopped)) {
                transition.accept(t);
            }
        });
    }
}
