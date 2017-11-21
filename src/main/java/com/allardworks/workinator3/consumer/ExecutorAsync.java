package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.core.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import static java.lang.System.out;

/**
 * Creates one thread per worker.
 */
@RequiredArgsConstructor
@Slf4j
public class ExecutorAsync extends ServiceBase {
    private final ConsumerConfiguration configuration;
    private final WorkerAsync worker;
    private final Coordinator coordinator;

    public ExecutorAsync(
            @NonNull final ConsumerConfiguration configuration,
            @NonNull final Worker worker,
            @NonNull final Coordinator coordinator
    ) {
        this.configuration = configuration;
        this.worker = (WorkerAsync)worker;
        this.coordinator = coordinator;
    }

    private ThreadService thread;

    private boolean canContinue(final Context context) {
        return context.getElapsed().compareTo(configuration.getMinWorkTime()) < 0;
    }

    private void run() {
        getServiceStatus().started();
        while (getServiceStatus().getStatus().isStarted()) {
            val assignment = coordinator.getAssignment(worker.getId());
            if (assignment == null) {
                // todo
                continue;
            }

            val context = new Context(this::canContinue, assignment, getServiceStatus());
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
        getServiceStatus().stopped();
    }

    @Override
    public void start() {
        getServiceStatus().initialize(s -> {
            // when starting, start the thread
            s.getEventHandlers().onPostStarting(t -> {
                thread = new ThreadService(this::run);
                thread.start();
            });
        });
        super.start();
    }

    @Override
    public void close() throws Exception {
        super.close();
        worker.close();
        if (thread != null) {
            thread.close();
            thread = null;
        }
    }
}