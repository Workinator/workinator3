package com.allardworks.workinator3.core;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static com.allardworks.workinator3.core.Status.*;

@Slf4j
public class ServiceStatus {
    private final Object syncroot = new Object();

    private final Set<Consumer<Transition>> transitionEventHandlers = new HashSet<>();

    private boolean initialized;

    @Getter
    private final TransitionEvents eventHandlers = new TransitionEvents(this);

    public ServiceStatus onTransition(@NonNull final Consumer<Transition> transitionHandler) {
        transitionEventHandlers.add(transitionHandler);
        return this;
    }

    public ServiceStatus clearEventHandlers(){
        transitionEventHandlers.clear();
        return this;
    }

    @Getter
    private Status status = Status.Stopped;


    /**
     * Thread safe method to execute initialization code.
     * The initialization method will only do work once regardless
     * of how many times it's called.
     * @param initializationMethod
     * @return
     */
    public ServiceStatus initialize(Consumer<ServiceStatus> initializationMethod) {
        synchronized (syncroot) {
            if (initialized) {
                return this;
            }

            initializationMethod.accept(this);
            initialized = true;
            return this;
        }
    }

    public boolean starting() {
        return transition(Stopped, Starting);
    }

    public boolean stopping() {
        return transition(Started, Stopping);
    }

    public boolean started() {
        return transition(Starting, Started);
    }

    public boolean stopped() {
        return transition(Stopping, Stopped);
    }

    private void executeHandlers(final Transition transition) {
        for (val h : transitionEventHandlers) {
            try {
                h.accept(transition);
            } catch (final Exception e) {
                // todo
                e.printStackTrace();
            }
        }
    }

    private boolean transition(final Status allowedOldStatus, final Status newStatus) {
        synchronized (syncroot) {
            if (!status.equals(allowedOldStatus)) {
                return false;
            }

            val before = new Transition(TransitionStage.BeforeTransition, allowedOldStatus, newStatus);
            executeHandlers(before);
            status = newStatus;
            val after = new Transition(TransitionStage.AfterTransition, allowedOldStatus, newStatus);
            executeHandlers(after);
            return true;
        }
    }
}
