package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.testsupport.DummyWorkinatorRepository;
import com.allardworks.workinator3.testsupport.DummyAsyncWorker;
import lombok.val;
import org.junit.Test;
import org.springframework.util.Assert;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ExecutorFactoryTests {
    @Test
    public void createAsyncExecutor() {
        val config = ConsumerConfiguration
                .builder()
                //.consumerName("b")
                .build();
        val worker = new DummyAsyncWorker();
        val factory = new ExecutorFactory(config, new DummyWorkinatorRepository());
        val id = new ExecutorId(new ConsumerRegistration(new ConsumerId("boo"), "asdfasdfasfd"), 1);
        //val executor = factory.createExecutor(id, worker);
        //Assert.isTrue(executor instanceof ExecutorAsync, "expected AsyncWorker");
        // TODO
        throw new NotImplementedException();
    }
}
