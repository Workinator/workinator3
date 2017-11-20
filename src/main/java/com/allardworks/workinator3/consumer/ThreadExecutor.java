package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.core.EventHandlers;
import com.allardworks.workinator3.core.ServiceStatus;
import com.allardworks.workinator3.core.ThreadService;
import com.allardworks.workinator3.core.Transition;
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

    private final EventHandlers contextStoppingEventHandlers = new EventHandlers();

    private ThreadService thread;

    private boolean canContinue(final Context context) {
        return context.getElapsed().compareTo(configuration.getMinWorkTime()) < 0;
    }

    private void run() {
        status.started();
        while (status.getStatus().isStarted()) {
            val assignment = coordinator.getAssignment(workerId);
            if (assignment == null) {
                // todo
                continue;
            }

            val context = new Context(this::canContinue, assignment, contextStoppingEventHandlers);
            while (context.canContinue()) {
                try {
                    worker.execute(context);
                    contextStoppingEventHandlers.clear();
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

    @Override
    public void start() {
        status.initialize(s -> {
            // when starting, start the thread
            s.onTransition(t -> {
                if (t.isPostStarting()) {
                    thread = new ThreadService(this::run);
                    thread.start();
                }
            });

            // when stop is called, fire the context events
            s.onTransition(t -> {
                if (t.isPostStopping()) {
                    // TODO: edge case race condition. Document as known issue.
                    // The event is intended for the SynchronousExecutor. Not worried
                    // about this unless it doens't work properly there.
                    // The executor is STARTED.
                    // The EXECUTE method is called.
                    // The executor is STOPPING before then execute method assigns the event handlers.
                    // The event handlers won't fire.
                    // That's tricky. however, it won't be an issue as long as the worker checks
                    // CanContinue before doing work.
                    // IE:     @Override
                    // public void execute(final WorkerContext context) {
                    //   // potential race condition
                    //   context.onStopping(() -> { /* do something */ });
                    //   // doesn't fix race condition, but eliminate it as a problem
                    //   if (!context.canContinue) {
                    //       // nevermind
                    //   }
                    contextStoppingEventHandlers.execute();
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
        worker.close();
        if (thread != null) {
            thread.close();
            thread = null;
        }
    }
}
