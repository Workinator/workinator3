package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.core.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates one thread per worker.
 */
@RequiredArgsConstructor
@Slf4j
public class ExecutorAsync extends ServiceBase {
    private final ExecutorId executorId;
    private final ConsumerConfiguration configuration;
    private final WorkerAsync worker;
    private final WorkinatorRepository workinatorRepository;

    private Assignment currentAssignment;
    private WorkerContext currentContext;
    private Thread thread;

    public Map<String, Object> getInfo() {
        val info = new HashMap<String, Object>();
        info.put("serviceStatus", getStatus().toString());
        info.put("executorId", executorId);
        info.put("currentAssignment", currentAssignment);
        info.put("currentContext", currentContext);
        return info;
    }

    private boolean canContinue(final Context context) {
        return context.getElapsed().compareTo(configuration.getMinWorkTime()) < 0;
    }

    private void run() {
        getServiceStatus().started();
        while (getServiceStatus().getStatus().isStarted()) {
            currentAssignment = workinatorRepository.getAssignment(executorId);
            if (currentAssignment == null) {
                // todo
                continue;
            }

            currentContext = new Context(this::canContinue, currentAssignment, getServiceStatus());
            while (currentContext.canContinue()) {
                try {
                    worker.execute(currentContext);
                    if (!currentContext.getHasMoreWork()) {
                        // no more work
                        break;
                    }
                } catch (final Exception e) {
                    log.error("worker.execute", e);
                    // TODO: rule engine. disable partition or try again.
                }
            }
            try {
                workinatorRepository.releaseAssignment(currentAssignment);
            } finally {
                currentAssignment = null;
                currentContext = null;
            }
        }
        getServiceStatus().stopped();
    }

    @Override
    public void start() {
        getServiceStatus().initialize(s -> {
            // when starting, start the thread
            s.getEventHandlers().onPostStarting(t -> {
                thread = new Thread(this::run);
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
            thread = null;
        }
    }
}
