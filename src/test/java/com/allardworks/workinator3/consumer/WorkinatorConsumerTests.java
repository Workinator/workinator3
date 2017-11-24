package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.testsupport.*;
import lombok.val;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class WorkinatorConsumerTests {
    @Test
    public void startAndStop() throws Exception {
        val configuration =
                ConsumerConfiguration
                        .builder()
                        .consumerName("yea")
                        .workerCount(5)
                        .build();

        val consumerId = new ConsumerId("boo", "yea");
        val registration = new ConsumerRegistration(consumerId, "whatever");
        val workerId = new WorkerId(registration, 1);
        val workinator = new DummyWorkinator();
        workinator.setNextAssignment(new Assignment(workerId, "blah", "asdfasfasfd"));

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
            assertEquals(configuration.getWorkerCount(), workers.size());

            // make sure all works do some work
            TestUtility.waitFor(() -> workers
                    .stream()
                    .filter(w -> w.getHitCount() < 100)
                    .count() == 0);
            TestUtility.stopAndWait(consumer);
        }
    }
}


