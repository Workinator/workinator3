package com.allardworks.workinator3.core;

import com.allardworks.workinator3.contracts.Service2;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class ThreadService implements Service2 {
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
    public void onTransition(Consumer<Transition> transitionHandler) {
        status.onTransition(transitionHandler);
    }

    @Override
    public void close() throws Exception {

    }
}
