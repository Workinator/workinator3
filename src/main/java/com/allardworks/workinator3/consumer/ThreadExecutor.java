package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.core.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.function.Consumer;

/**
 * Creates one thread per worker.
 */
@Slf4j
@RequiredArgsConstructor
public class ThreadExecutor implements Service2 {
    @NonNull
    private final ConsumerConfiguration configuration;

    @NonNull
    private final WorkerId workerId;

    @NonNull
    private final Worker worker;

    @NonNull
    private final Coordinator coordinator;

    private final ServiceStatus status = new ServiceStatus();

    private ThreadService thread;

    private boolean canContinue(final Context context) {
        return context.getElapsed().compareTo(configuration.getMinWorkTime()) < 0;
    }

    private void run() {
        status.started();
        val contextStoppingHandlers = new EventHandlers();
        while (status.getStatus().isStarted()) {
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

        status.stopped();
    }

    /*
    @Override
    public void close() {
        super.close();
        if (thread == null) {
            return;
        }

        thread.close();
        thread = null;
    }*/

    @Override
    public void start() {
        status.initialize(s -> {
           s.onTransition(t -> {
               if (t.isPostStarting()) {
                    thread = new ThreadService(this::run);
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
        if (thread != null) {
            thread.close();
            thread = null;
        }
    }
}
