package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.testsupport.*;
import lombok.val;
import org.junit.Test;

import java.util.ArrayList;

import static com.allardworks.workinator3.testsupport.TestUtility.stopAndWait;
import static com.allardworks.workinator3.testsupport.TestUtility.waitFor;
import static org.junit.Assert.assertEquals;

public class WorkinatorRepositoryConsumerTests {
    @Test
    public void startAndStop() throws Exception {
        val configuration =
                ConsumerConfiguration
                        .builder()
                        //.consumerName("yea")
                        .maxExecutorCount(1)
                        .build();

        val consumerId = new ConsumerId("booyea");
        val registration = new ConsumerRegistration(consumerId, "whatever");
        val workerId = new ExecutorId(registration, 1);
        val workinator = new DummyWorkinatorRepository();
        workinator.setNextAssignment(new Assignment(workerId, "ab", 0, ""));

        val executorSupplier = new ExecutorFactory(configuration, workinator);

        val workers = new ArrayList<DummyAsyncWorker>();
        val workerFactory = new DummyWorkerFactory(() -> {
            val w = new DummyAsyncWorker();
            workers.add(w);

            // lombok requires the cast even though java does not
            return (AsyncWorker) w;
        });

        try (val consumer = new WorkinatorConsumer(configuration, workinator, executorSupplier, workerFactory, consumerId)) {
            consumer.start();
            TestUtility.startAndWait(consumer);
            Thread.sleep(1000);
            assertEquals(configuration.getMaxExecutorCount(), workers.size());

            // make sure all works do some work
            waitFor(() -> workers
                    .stream()
                    .filter(w -> w.getHitCount() < 25)
                    .count() == 0);
            stopAndWait(consumer);
        }
    }
}


