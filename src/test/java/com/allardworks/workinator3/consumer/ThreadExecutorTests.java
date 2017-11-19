package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.testsupport.DummyCoordinator;
import com.allardworks.workinator3.testsupport.DummyWorker;
import com.allardworks.workinator3.testsupport.TestUtility;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

public class ThreadExecutorTests {

    /**
     * Starts an executor.
     * Makes sure it does work.
     * Then stops it.
     *
     * @throws InterruptedException
     */
    @Test
    public void startAndStop() throws InterruptedException {
        val configuration = ConsumerConfiguration.builder().consumerName("boo").partitionType("yea").build();
        val consumerId = new ConsumerId("boo", "yea");
        val registration = new ConsumerRegistration(consumerId, "whatever");
        val workerId = new WorkerId(registration, 1);
        val coordinator = new DummyCoordinator();
        val worker = new DummyWorker();

        coordinator.setNextAssignment(new Assignment(workerId, "blah", "asdfasfasfd"));
        try (val executor = new ThreadExecutor(configuration, workerId, worker, coordinator)) {
            TestUtility.startAndWait(executor);
            TestUtility.waitFor(() -> worker.getLastContext() != null);
            TestUtility.stopAndWait(executor);
            Assert.assertTrue(worker.getHitCount() > 0);
        }
    }

    @Test
    public void wontStopWhileWorkerIsBusy() {
        val freezeTime = 750;
        val configuration = ConsumerConfiguration.builder().consumerName("boo").partitionType("yea").build();
        val consumerId = new ConsumerId("boo", "yea");
        val registration = new ConsumerRegistration(consumerId, "whatever");
        val workerId = new WorkerId(registration, 1);
        val coordinator = new DummyCoordinator();
        val worker = new DummyWorker();

        coordinator.setNextAssignment(new Assignment(workerId, "blah", "asdfasfasfd"));
        try (val executor = new ThreadExecutor(configuration, workerId, worker, coordinator)) {
            TestUtility.startAndWait(executor);
            TestUtility.waitFor(() -> worker.getLastContext() != null);

            // freeze the thread
            worker.setFrozen(true);

            // thaw it after some time
            new Thread(() -> {
                try {
                    Thread.sleep(freezeTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                worker.setFrozen(false);
            }).start();

            val start = System.currentTimeMillis();
            TestUtility.stopAndWait(executor);
            val stop = System.currentTimeMillis();

            // make sure the stop took as long as it should have
            Assert.assertTrue(stop - start >= freezeTime);
            Assert.assertTrue(worker.getHitCount() > 0);
        }
    }
}
