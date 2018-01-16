package com.allardworks.workinator3.repository;

import com.allardworks.workinator3.contracts.ConsumerDao;
import com.allardworks.workinator3.contracts.ConsumerExistsException;
import com.allardworks.workinator3.testsupport.RepositoryTester;
import lombok.val;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public abstract class RepositoryTests {
    public abstract RepositoryTester getRepoTester();

    @Test
    public void createCantCreateDuplicateConsumer() throws Exception {
        try (val tester = getRepoTester()) {
            val consumer = new ConsumerDao();
            consumer.setConsumerRegistration(UUID.randomUUID().toString());
            consumer.setConsumerId("blah blah");
            consumer.getMaxExecutorCount().setValue(10);

            tester.getRepository().createConsumer(consumer);
            try {
                tester.getRepository().createConsumer(consumer);
                fail("Second createPartitions should've failed because the partition already exists");
            } catch (final ConsumerExistsException e) {
                assertEquals(consumer.getConsumerId(), e.getConsumerId());
            }
        }
    }
}
