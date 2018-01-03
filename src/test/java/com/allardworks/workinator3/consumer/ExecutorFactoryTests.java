package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.ConsumerConfiguration;
import com.allardworks.workinator3.contracts.WorkerAsync;
import com.allardworks.workinator3.testsupport.DummyWorkinatorRepository;
import com.allardworks.workinator3.testsupport.DummyWorkerAsync;
import lombok.val;
import org.junit.Test;
import org.springframework.util.Assert;

public class ExecutorFactoryTests {
    @Test
    public void createAsyncExecutor() {
        val config = ConsumerConfiguration
                .builder()
                //.consumerName("b")
                .build();
        val worker = new DummyWorkerAsync();
        val factory = new ExecutorFactory(config, new DummyWorkinatorRepository());
        val executor = factory.createExecutor(worker);
        Assert.isTrue(worker instanceof WorkerAsync, "just to prove the point");
        Assert.isTrue(executor instanceof ExecutorAsync, "expected WorkerAsync");
    }
}
