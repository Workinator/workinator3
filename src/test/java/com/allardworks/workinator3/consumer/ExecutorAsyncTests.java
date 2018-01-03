package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.testsupport.DummyWorkinatorRepository;
import com.allardworks.workinator3.testsupport.DummyWorkerAsync;
import com.allardworks.workinator3.testsupport.TestUtility;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

import static com.allardworks.workinator3.testsupport.TestUtility.*;

public class ExecutorAsyncTests {

    /**
     * Starts an executor.
     * Makes sure it does work.
     * Then stops it.
     *
     * @throws InterruptedException
     */
    @Test
    public void startAndStop() throws Exception {
        val configuration = ConsumerConfiguration
                .builder()
                //.consumerName("boo")
                .build();
        val consumerId = new ConsumerId("booyea");
        val registration = new ConsumerRegistration(consumerId, "whatever");
        val workerId = new ExecutorId(registration, 1);
        val workinator = new DummyWorkinatorRepository();
        val worker = new DummyWorkerAsync();

        workinator.setNextAssignment(new Assignment(workerId, new Partition("ab"), "", 1));

        try (val executor = new ExecutorAsync(configuration, worker, workinator)) {
            startAndWait(executor);
            TestUtility.waitFor(() -> worker.getLastContext() != null);
            TestUtility.stopAndWait(executor);
            Assert.assertTrue(worker.getHitCount() > 0);
        }
    }

    /**
     * Put the worker into an infinite loop (freeze), then
     * stop the executor. See that the STOP won't complete
     * until the worker is thawed.
     *
     * @throws Exception
     */
    @Test
    public void wontStopWhileWorkerIsBusy() throws Exception {
        val freezeTime = 100;
        val configuration = ConsumerConfiguration
                .builder()
                //.consumerName("boo")
                .build();
        val consumerId = new ConsumerId("booyea");
        val registration = new ConsumerRegistration(consumerId, "whatever");
        val workerId = new ExecutorId(registration, 1);
        val workinator = new DummyWorkinatorRepository();
        val worker = new DummyWorkerAsync();

        workinator.setNextAssignment(new Assignment(workerId, new Partition("ab"), "", 1));
        try (val executor = new ExecutorAsync(configuration, worker, workinator)) {
            startAndWait(executor);
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

