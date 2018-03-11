package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.consumer.config.ConsumerConfiguration;
import com.allardworks.workinator3.contracts.ConsumerId;
import com.allardworks.workinator3.contracts.ConsumerRegistration;
import com.allardworks.workinator3.contracts.WorkerId;
import com.allardworks.workinator3.testsupport.DummyAsyncWorker;
import com.allardworks.workinator3.testsupport.DummyAsyncWorkerFactory;
import com.allardworks.workinator3.testsupport.DummyWorkinator;
import lombok.val;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;


public class ExecutorFactoryTests {
    /**
     * The type of executor factory necessary is based on the type of work factory
     * that is passed in. AsyncWorkerFactory results in the generation of an AsyncExecutorFactory.
     */
    @Test
    public void createAsyncExecutor() {
        val config = ConsumerConfiguration
                .builder()
                .build();
        val workerFactory = new DummyAsyncWorkerFactory(DummyAsyncWorker::new);
        val factory = new ExecutorFactory(config, new DummyWorkinator());
        val id = new WorkerId(new ConsumerRegistration(new ConsumerId("boo"), ""), 1);
        val executor = factory.createExecutor(id, workerFactory);
        assertNotNull(executor);
    }
}
