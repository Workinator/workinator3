package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.ConsumerConfiguration;
import com.allardworks.workinator3.contracts.ConsumerId;
import com.allardworks.workinator3.testsupport.DummyCoordinator;
import com.allardworks.workinator3.testsupport.DummyWorkerDelegate;
import com.allardworks.workinator3.testsupport.DummyWorkerFactory;
import com.allardworks.workinator3.testsupport.TestUtility;
import lombok.val;
import org.junit.Test;

import static java.lang.System.out;

public class CoordinatorConsumerTests {
    @Test
    public void startAndStop() throws Exception {
        val configuration =
                ConsumerConfiguration
                        .builder()
                        .partitionType("boo")
                        .consumerName("yea")
                        .workerCount(5)
                        .build();

        val consumerId = new ConsumerId("adfafasf", "asdfasf");

        val coordinator =new DummyCoordinator();
        val executorSupplier = new ExecutorFactory(configuration, coordinator);
        val worker = new DummyWorkerDelegate(c -> {});
        val workerSupplier = new DummyWorkerFactory(() -> worker);
        try (val consumer = new CoordinatorConsumer(configuration, coordinator, executorSupplier, workerSupplier, consumerId)) {
            consumer.start();
            TestUtility.startAndWait(consumer);
            out.println();
        }
    }
}


