package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.core.EventHandlers;
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
    /**
     * Configuration for this consumer.
     */
    @NonNull
    private final ConsumerConfiguration configuration;

    /**
     * The coordinator. Provides the partition assignments per worker.
     */
    @NonNull
    private final Coordinator coordinator;

    /**
     * Creates the executors.
     */
    @NonNull
    private final ExecutorSupplier executorSupplier;

    /**
     * The ID of this consumer.
     */
    @NonNull
    private final ConsumerId consumerId;

    /**
     * One executor per worker.
     */
    private List<Service> executors;

    /**
     * The consumer's registration. Returned by the coordinator's register method.
     */
    private ConsumerRegistration registration;

    /**
     * Things to do when the service finishes starting.
     */
    private final EventHandlers onStart = new EventHandlers();

    /**
     * Things to do when the service finishes stopping.
     */
    private final EventHandlers onStop = new EventHandlers();

    /**
     * Tracks how many executors have stopped. When 0, all done. Fire the stopped event.
     */
    private CountDownLatch startCount;

    /**
     * Tracks how many executors have started. When 0, all done. Fire the started event.
     */
    private CountDownLatch stopCount;

    /**
     * Start the service.
     * Registers this consumer and sets up the executors.
     * @param onStartComplete
     */
    @Override
    protected void startService(Runnable onStartComplete) {
        onStart.add(onStartComplete);
        startCount = new CountDownLatch(configuration.getWorkerCount());
        stopCount = new CountDownLatch(configuration.getWorkerCount());
        setupConsumer();
        setupAndStartExecutors();
    }

    /**
     * Stops the servic.e
     * @param onStopComplete
     */
    @Override
    protected void stopService(Runnable onStopComplete) {
        onStop.add(onStopComplete);
        for (val executor : executors) {
            executor.stop();
        }
    }

    /**
     * Create an executor for each worker.
     */
    private void setupAndStartExecutors() {
        // create the worker ids
        val workerIds = IntStream
                .range(0, configuration.getWorkerCount())
                .mapToObj(i -> new WorkerId(registration, i))
                .collect(Collectors.toList());

        // create an executor for each worker
        val executors = workerIds
                .stream()
                .map(executorSupplier::create)
                .collect(Collectors.toList());

        // initialize and start the executors
        for(val executor : executors) {
            // setup start and stop events
            executor.onStarted(this::onExecutorStarted);
            executor.onStopped(this::onExecutorStopped);

            // start the executors
            executor.start();
        }

        this.executors = executors;
    }

    /**
     * Register this consumer with the coordinator.
     */
    private void setupConsumer() {
        registration = coordinator.registerConsumer(null);
    }

    /**
     * Event handler for executer.started.
     * @param executor
     */
    private void onExecutorStarted(final Service executor) {
        startCount.countDown();
        if (startCount.getCount() == 0) {
            onStart.execute();
            onStart.clear();
        }
    }

    /**
     * Event handler for executor.stopped.
     * @param executor
     */
    private void onExecutorStopped(final Service executor) {
        stopCount.countDown();
        if (stopCount.getCount() == 0) {
            cleanupExecutors();
            onStop.execute();
            onStop.clear();
        }
    }

    /**
     * close the executors.
     * Clear the list when done.
     */
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
}
