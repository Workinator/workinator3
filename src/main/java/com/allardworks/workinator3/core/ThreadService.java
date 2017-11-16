package com.allardworks.workinator3.core;

import com.allardworks.workinator3.contracts.Service;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class ThreadService extends ServiceBase {
    @NonNull private final Runnable method;
    private Thread thread;
    private Runnable onStart;
    private Runnable onStop;


    @Override
    protected void startService(Runnable onStartComplete) {
        onStart = onStartComplete;
        thread = new Thread(this::run);
        thread.start();
    }

    private void run() {
        // signal work has started
        val start = onStart;
        onStart = null;
        if (start != null) {
            start.run();
        }

        // execute the work
        method.run();

        // signal work has completed
        val stop = onStop;
        onStop = null;
        if (stop != null) {
            stop.run();
        }
    }

    @Override
    protected void stopService(Runnable onStopComplete) {
        onStop = onStopComplete;
    }

    @Override
    public Service unsubscribe() {
        return null;
    }
}
