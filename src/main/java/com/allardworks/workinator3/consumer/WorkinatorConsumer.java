package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.core.ServiceBase;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
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
    private final Workinator client;

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
                try {
                    // TODO: if consumer already exists, or other exception,
                    // then the wc is stuck in starting. need to work out proper error handling.
                    setupConsumer();
                    setupAndStartExecutors();
                } catch (ConsumerExistsException e) {
                    e.printStackTrace();
                }
            });

            s.getEventHandlers().onPostStopping(t -> {
                for (val executor : executors) {
                    executor.stop();
                }
            });
        });
        super.start();
    }

    public Map<String, Object> getInfo() {
        val map = new HashMap<String, Object>();
        map.put("consumerId", consumerId);
        map.put("serviceStatus", getStatus().toString());

        val exeuctorInfo = executors.stream().map(Service::getInfo).collect(toList());
        map.put("executors", exeuctorInfo);
        return map;
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

        executors = new ArrayList<>();
        for (val ex : executorIds) {
            val executor = executorFactory.createExecutor(ex, workerFactory);
            executors.add(executor);

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
    private void setupConsumer() throws ConsumerExistsException {
        registration = client.registerConsumer(consumerId);
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
