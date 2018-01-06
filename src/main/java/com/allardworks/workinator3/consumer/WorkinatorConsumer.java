package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.core.ServiceBase;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

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
    private final WorkinatorRepository workinatorRepository;

    /**
     * Creates the executors.
     */
    @NonNull
    private final ExecutorFactory executorFactory;

    /**
     * Creates the workers.
     */
    @NonNull
    private final WorkerFactory workerFactory;

    /**
     * The ID of this consumer.
     */
    @NonNull
    private final ConsumerId consumerId;

    /**
     * The consumer's registration. Returned by the workinator's register method.
     */
    private ConsumerRegistration registration;

    /**
     * One executor per worker.
     */
    private List<Service> executors;


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
                startCount = new CountDownLatch(configuration.getMaxExecutorCount());
                stopCount = new CountDownLatch(configuration.getMaxExecutorCount());
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
        // createPartitions the worker ids
        val executorIds = IntStream
                .range(0, configuration.getMaxExecutorCount())
                .mapToObj(i -> new ExecutorId(registration, i))
                .collect(toList());

        // createPartitions the workers
        val workers = executorIds
                .stream()
                .map(workerFactory::createWorker)
                .collect(toList());

        // createPartitions an executor for each worker
        executors = workers
                .stream()
                .map(executorFactory::createExecutor)
                .collect(toList());

        // initialize and start the executors
        for(val executor : executors) {
            // createPartitions start and stop events
            executor.getTransitionEventHandlers().onPostStarted(t -> onExecutorStarted());
            executor.getTransitionEventHandlers().onPostStopped(t -> onExecutorStopped());

            // start the executor
            executor.start();
        }
    }

    /**
     * Register this consumer with the workinator.
     */
    private void setupConsumer() {
        registration = workinatorRepository.registerConsumer(consumerId);
        if (registration == null) {
            throw new RuntimeException("Critcal problem... ");
        }
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
