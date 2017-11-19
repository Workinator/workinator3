package com.allardworks.workinator3.core;

import com.allardworks.workinator3.contracts.Service;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class ThreadService extends ServiceBase {
    @NonNull
    private final Runnable method;
    private Thread thread;
    private EventHandlers onStart = new EventHandlers();
    private EventHandlers onStop = new EventHandlers();


    @Override
    protected void startService(Runnable onStartComplete) {
        onStart.add(onStartComplete);
        thread = new Thread(this::run);
        thread.start();
    }

    private void run() {
        // signal work has started
        val start = onStart;
        onStart = null;
        start.execute();
        start.clear();

        // execute the work
        method.run();

        // signal work has completed
        onStop.execute();
        onStop.clear();
    }

    @Override
    protected void stopService(Runnable onStopComplete) {
        onStop.add(onStopComplete);
    }

    @Override
    public Service unsubscribe() {
        return null;
    }
}
