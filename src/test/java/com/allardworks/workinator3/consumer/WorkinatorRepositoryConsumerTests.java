package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.testsupport.*;
import lombok.val;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class WorkinatorRepositoryConsumerTests {
    @Test
    public void startAndStop() throws Exception {
        val configuration =
                ConsumerConfiguration
                        .builder()
                        //.consumerName("yea")
                        .maxExecutorCount(5)
                        .build();

        val consumerId = new ConsumerId("booyea");
        val registration = new ConsumerRegistration(consumerId, "whatever");
        val workerId = new ExecutorId(registration, 1);
        val workinator = new DummyWorkinatorRepository();
        workinator.setNextAssignment(new Assignment(workerId, new Partition("ab"), "", 1));

        val executorSupplier = new ExecutorFactory(configuration, workinator);

        val workers = new ArrayList<DummyWorkerAsync>();
        val workerFactory = new DummyWorkerFactory(() -> {
            val w = new DummyWorkerAsync();
            workers.add(w);

            // lombok requires the cast even though java does not
            return (WorkerAsync) w;
        });

        try (val consumer = new WorkinatorConsumer(configuration, workinator, executorSupplier, workerFactory, consumerId)) {
            consumer.start();
            TestUtility.startAndWait(consumer);
            assertEquals(configuration.getMaxExecutorCount(), workers.size());

            // make sure all works do some work
            TestUtility.waitFor(() -> workers
                    .stream()
                    .filter(w -> w.getHitCount() < 100)
                    .count() == 0);
            TestUtility.stopAndWait(consumer);
        }
    }
}


