package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.core.ServiceBase;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;

@RequiredArgsConstructor
@Slf4j
public class WorkinatorConsumer extends ServiceBase {
    /**
     * Configuration for this consumer.
     */
    @NonNull
    private final ConsumerConfiguration configuration;

    /**
     * The workinator. Provides the partition assignments per worker.
     */
    @NonNull
    private final Workinator workinator;

    /**
     * Creates the executors.
     */
    @NonNull
    private final ExecutorFactory executorFactory;

    /**
     * Creates a workers.
     */
    @NonNull
    private final WorkerFactory workerFactory;

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
     * The consumer's registration. Returned by the workinator's register method.
     */
    private ConsumerRegistration registration;

    /**
     * Tracks how many executors have stopped. When 0, all done. Fire the stopped event.
     */
    private CountDownLatch startCount;

    /**
     * Tracks how many executors have started. When 0, all done. Fire the started event.
     */
    private CountDownLatch stopCount;

    @Override
    public void start() {
        getServiceStatus().initialize(s -> {
            s.getEventHandlers().onPostStarting(t -> {
                startCount = new CountDownLatch(configuration.getWorkerCount());
                stopCount = new CountDownLatch(configuration.getWorkerCount());
                setupConsumer();
                setupAndStartExecutors();
            });

            s.getEventHandlers().onPostStopping(t -> {
                for (val executor : executors) {
                    executor.stop();
                }
            });
        });
        super.start();
    }

    /**
     * Create an executor for each worker.
     */
    private void setupAndStartExecutors() {
        // create the worker ids
        val workerIds = IntStream
                .range(0, configuration.getWorkerCount())
                .mapToObj(i -> new WorkerId(registration, i))
                .collect(toList());

        // create the workers
        val workers = workerIds
                .stream()
                .map(workerFactory::createWorker)
                .collect(toList());

        // create an executor for each worker
        val executors = workers
                .stream()
                .map(executorFactory::createExecutor)
                .collect(toList());

        // initialize and start the executors
        for(val executor : executors) {
            // setup start and stop events
            executor.getTransitionEventHandlers().onPostStarted(t -> onExecutorStarted());
            executor.getTransitionEventHandlers().onPostStopped(t -> onExecutorStopped());

            // start the executors
            executor.start();
        }

        this.executors = executors;
    }

    /**
     * Register this consumer with the workinator.
     */
    private void setupConsumer() {
        registration = workinator.registerConsumer(consumerId);
    }

    /**
     * Event handler for executer.started.
     */
    private void onExecutorStarted() {
        startCount.countDown();
        if (startCount.getCount() == 0) {
            getServiceStatus().started();
        }
    }

    /**
     * Event handler for executor.stopped.
     */
    private void onExecutorStopped() {
        stopCount.countDown();
        if (stopCount.getCount() == 0) {
            cleanupExecutors();
            getServiceStatus().stopped();
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
