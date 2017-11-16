package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.Worker;
import com.allardworks.workinator3.core.ServiceBase;
import com.allardworks.workinator3.core.ThreadService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Creates one thread per worker.
 */
@Slf4j
@RequiredArgsConstructor
public class ThreadExecutor extends ServiceBase {
    @NonNull
    private final Worker worker;

    private ThreadService thread;
    private Runnable startComplete;
    private Runnable stopComplete;

    @Override
    protected void startService(@NonNull Runnable onStartComplete) {
        startComplete = onStartComplete;
        thread = new ThreadService(this::run);
        thread.start();
    }

    @Override
    protected void stopService(@NonNull Runnable onStopComplete) {
        stopComplete = onStopComplete;
        thread.close();
        thread = null;
    }

    protected void run() {
        startComplete.run();
        startComplete = null;

        while (getStatus().isStarted()) {
            try {
                worker.execute(null);
            } catch (final Exception e) {
                log.error("worker.execute", e);
            }
        }
        stopComplete.run();
        stopComplete = null;
    }
}
