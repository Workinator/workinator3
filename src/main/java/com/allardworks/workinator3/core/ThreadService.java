package com.allardworks.workinator3.core;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ThreadService extends ServiceBase {
    @NonNull
    private final Runnable method;
    private Thread thread;


    @Override
    protected void startingService() {
        thread = new Thread(this::run);
        thread.start();
    }

    private void run() {
        signalStartingComplete();

        // execute the work
        method.run();

        signalStoppingComplete();
    }

    @Override
    protected void stoppingService() {
    }
}
