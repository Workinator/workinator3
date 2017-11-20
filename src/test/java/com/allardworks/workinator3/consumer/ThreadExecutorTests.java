package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.testsupport.DummyCoordinator;
import com.allardworks.workinator3.testsupport.DummyWorker;
import com.allardworks.workinator3.testsupport.TestUtility;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ThreadExecutorTests {

    /**
     * Starts an executor.
     * Makes sure it does work.
     * Then stops it.
     *
     * @throws InterruptedException
     */
    @Test
    public void startAndStop() throws Exception {
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
    public void wontStopWhileWorkerIsBusy() throws Exception {
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

    /**
     * When STOP is called, the context stop event will be called.
     * This only happens when EXECUTE is in progress.
     * Use FREEZE to make sure it's in progress. The stop event will thaw it.
     * Everything will stop, and we'll see that the stop event was hit.
     * @throws Exception
     */
    @Test
    public void contextEventFires() throws Exception {
        val configuration = ConsumerConfiguration.builder().consumerName("boo").partitionType("yea").build();
        val consumerId = new ConsumerId("boo", "yea");
        val registration = new ConsumerRegistration(consumerId, "whatever");
        val workerId = new WorkerId(registration, 1);
        val coordinator = new DummyCoordinator();
        val worker = new DummyWorker();

        // freeze the thread
        worker.setFrozen(true);
        worker.setThawOnStop(true);
        // thaw when the executor is stopped.

        coordinator.setNextAssignment(new Assignment(workerId, "blah", "asdfasfasfd"));
        try (val executor = new ThreadExecutor(configuration, workerId, worker, coordinator)) {
            TestUtility.startAndWait(executor);
            TestUtility.waitFor(() -> worker.getLastContext() != null);
            assertEquals(0, worker.getContextStopEventHitCount().get());
            TestUtility.stopAndWait(executor);

            assertEquals(1, worker.getContextStopEventHitCount().get());
        }
    }

    /**
     * The context.onStopping event was intended for the
     * SynchronousExecutor, not ThreadExecutor. However,
     * it may be useful
     */
    @Test
    public void contextEventFiresRaceCondition() {

    }

}
