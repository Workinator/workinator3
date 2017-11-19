package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.core.EventHandlers;
import com.allardworks.workinator3.core.ServiceBase;
import com.allardworks.workinator3.core.ThreadService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Creates one thread per worker.
 */
@Slf4j
@RequiredArgsConstructor
public class ThreadExecutor extends ServiceBase {
    @NonNull
    private final ConsumerConfiguration configuration;

    @NonNull
    private final WorkerId workerId;

    @NonNull
    private final Worker worker;

    @NonNull
    private final Coordinator coordinator;

    private ThreadService thread;
    private final EventHandlers startComplete = new EventHandlers();
    private final EventHandlers stopComplete = new EventHandlers();

    @Override
    protected void startService(@NonNull Runnable onStartComplete) {
        startComplete.add(onStartComplete);
        thread = new ThreadService(this::run);
        thread.start();
    }

    @Override
    protected void stopService(@NonNull Runnable onStopComplete) {
        // save the stop event handler. fire it later when stop completes.
        stopComplete.add(onStopComplete);
    }

    private boolean canContinue(final Context context) {
        return context.getElapsed().compareTo(configuration.getMinWorkTime()) < 0;
    }

    private void run() {
        startComplete.execute();
        startComplete.clear();

        val contextStoppingHandlers = new EventHandlers();
        while (getStatus().isStarted()) {
            val assignment = coordinator.getAssignment(workerId);
            if (assignment == null) {
                // todo
                continue;
            }

            val context = new Context(this::canContinue, assignment, contextStoppingHandlers.clear());
            while (context.canContinue()) {
                try {
                    worker.execute(context);
                    if (!context.getHasMoreWork()) {
                        // no more work
                        break;
                    }
                } catch (final Exception e) {
                    log.error("worker.execute", e);
                    // TODO: rule engine. disable partition or try again.
                }
            }
        }
        // execute the context's stopping event handlers.
        contextStoppingHandlers.execute();
        stopComplete.execute();
        stopComplete.clear();
    }

    @Override
    public void close() {
        super.close();
        thread.close();
        thread = null;
    }
}
