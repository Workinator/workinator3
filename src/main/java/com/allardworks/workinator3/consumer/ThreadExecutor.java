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

    @Override
    protected void startingService() {
        thread = new ThreadService(this::run);
        thread.start();
    }

    @Override
    protected void stoppingService() {
    }

    private boolean canContinue(final Context context) {
        return context.getElapsed().compareTo(configuration.getMinWorkTime()) < 0;
    }

    private void run() {
        signalStartingComplete();
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
        close();
        signalStoppingComplete();
    }

    @Override
    public void close() {
        super.close();
        if (thread == null) {
            return;
        }

        thread.close();
        thread = null;
    }
}
