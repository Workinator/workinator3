package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.core.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.System.out;

/**
 * Creates one thread per worker.
 */
@Slf4j
public class ExecutorAsync extends ServiceBase {
    private final WorkerRunnerProvider runnerProvider;
    private final ExecutorId id;
    private final ConsumerConfiguration consumerConfiguration;
    private Thread thread;

    public ExecutorAsync(
            final ExecutorId executorId,
            final ConsumerConfiguration configuration,
            final AsyncWorkerFactory workerFactory,
            final WorkinatorRepository workinatorRepository) {

        // can continue = true for the partition's minimum work time
        //Function<Context, Boolean> canContinue = c -> ;
        consumerConfiguration = configuration;
        runnerProvider = new WorkerRunnerProvider(this::canContinue, workerFactory, workinatorRepository, executorId, getServiceStatus());
        id = executorId;
    }

    private boolean canContinue(final Context context) {
        return getStatus().isStarted() && context.getElapsed().compareTo(consumerConfiguration.getMinWorkTime()) < 0;
    }

    public Map<String, Object> getInfo() {
        val info = new HashMap<String, Object>();
        info.put("serviceStatus", getStatus().toString());
        info.put("executorId", id);
        info.put("currentAssignment", runnerProvider.getCurrentAssignment());
        info.put("currentContext", runnerProvider.getCurrentContext());
        return info;
    }

    private void run() {
        try {
            getServiceStatus().started();
            while (getServiceStatus().getStatus().isStarted()) {
                out.println("-- before lookup");
                val runner = runnerProvider.lookupRunner();
                out.println("-- after lookup");
                if (runner == null) {
                    // TODO
                    continue;
                }

                runner.run();
            }
            out.println("====================== out");
            try {
                runnerProvider.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            getServiceStatus().stopped();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @Override
    public void start() {
        getServiceStatus().initialize(s -> {
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
        if (thread != null) {
            thread = null;
        }
        runnerProvider.close();
    }
}
