package com.allardworks.workinator3.core;

import com.allardworks.workinator3.contracts.Service;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

/**
 * Service wrapper to manage a thread.
 */
@RequiredArgsConstructor
public class ThreadService implements Service {
    // TODO: switch to servicebase
    private final ServiceStatus status = new ServiceStatus();

    @NonNull
    private final Runnable method;
    private Thread thread;

    private void run() {
        status.started();

        // execute the work
        try {
            method.run();
        } catch (final Exception e) {
            // todo
        }

        status.stopped();
    }

    @Override
    public void start() {
        status.initialize(s -> {
            s.onTransition(t -> {
                if (t.isPostStarting()) {
                    thread = new Thread(this::run);
                    thread.start();
                }
            });
        });
        status.starting();
    }

    @Override
    public void stop() {
        status.stopping();
    }

    @Override
    public Status getStatus() {
        return status.getStatus();
    }

    @Override
    public TransitionEvents getTransitionEventHandlers() {
        return status.getEventHandlers();
    }

    @Override
    public void close() throws Exception {
        thread = null;
    }
}
