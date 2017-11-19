package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.testsupport.DummyCoordinator;
import com.allardworks.workinator3.testsupport.DummyWorker;
import com.allardworks.workinator3.testsupport.TestUtility;
import lombok.val;
import org.junit.Test;

public class ThreadExecutorTests {

    @Test
    public void blah() throws InterruptedException {
        val configuration = ConsumerConfiguration.builder().consumerName("boo").partitionType("yea").build();

        val consumerId = new ConsumerId("boo", "yea");
        val registration = new ConsumerRegistration(consumerId, "whatever");
        val workerId = new WorkerId(registration, 1);

        val coordinator = new DummyCoordinator();
        val worker = new DummyWorker();

        coordinator.setNextAssignment(new Assignment(workerId, "blah", "asdfasfasfd"));
        val executor = new ThreadExecutor(configuration, workerId, worker, coordinator);
        TestUtility.startAndWait(executor);

        TestUtility.waitFor(() -> worker.getLastContext() != null);

        TestUtility.stopAndWait(executor);
    }
}
