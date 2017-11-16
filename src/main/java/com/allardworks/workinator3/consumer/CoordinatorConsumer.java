package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.core.ServiceBase;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
@Slf4j
public class CoordinatorConsumer extends ServiceBase  {
    @NonNull
    private final ConsumerConfiguration configuration;

    @NonNull
    private final Coordinator coordinator;

    @NonNull
    private final ExecutorSupplier executorSupplier;

    @NonNull
    private final WorkerSupplier workerSupplier;

    private List<Service> executors;
    private ConsumerRegistration registration;
    private Runnable onStart;
    private Runnable onStop;
    private CountDownLatch startCount;
    private CountDownLatch stopCount;

    private void setupAndStartExecutors() {
        val workerIds = IntStream
                .range(0, configuration.getWorkerCount())
                .mapToObj(i -> new WorkerId(registration, i))
                .collect(Collectors.toList());

        val workers = workerIds
                .stream()
                .map(workerSupplier::getWorker)
                .collect(Collectors.toList());

        val executors = workers
                .stream()
                .map(executorSupplier::create)
                .collect(Collectors.toList());

        for(val executor : executors) {
            // setup start and stop events
            executor.onStarted(this::onExecutorStarted);
            executor.onStopped(this::onExecutorStopped);

            // start the executors
            executor.start();
        }

        this.executors = executors;
    }

    private void setupConsumer() {
        registration = coordinator.registerConsumer(null);
    }

    @Override
    protected void startService(Runnable onStartComplete) {
        onStart = onStartComplete;
        startCount = new CountDownLatch(configuration.getWorkerCount());
        stopCount = new CountDownLatch(configuration.getWorkerCount());
        setupConsumer();
        setupAndStartExecutors();
    }

    private void onExecutorStarted(final Service executor) {
        startCount.countDown();
        if (startCount.getCount() == 0 && onStart != null) {
            onStart.run();
        }
    }

    private void onExecutorStopped(final Service executor) {
        stopCount.countDown();
        if (stopCount.getCount() == 0 && onStop != null) {
            cleanupExecutors();
            onStop.run();
        }
    }

    private void cleanupExecutors() {
        for (val e : executors) {
            try {
                e.close();
            } catch (Exception ex) {
                log.error("Closing executor", ex);
            }
        }
        executors.clear();
    }

    @Override
    protected void stopService(Runnable onStopComplete) {
        onStop = onStopComplete;
        for (val executor : executors) {
            executor.stop();
        }
    }
}
