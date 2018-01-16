package com.allardworks.workinator3.repository;

import com.allardworks.workinator3.contracts.ConsumerDao;
import com.allardworks.workinator3.contracts.ConsumerDoesntExistsException;
import com.allardworks.workinator3.contracts.ConsumerExistsException;
import com.allardworks.workinator3.testsupport.RepositoryTester;
import lombok.val;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public abstract class RepositoryTests {
    public abstract RepositoryTester getRepoTester();

    @Test
    public void createCantCreateDuplicateConsumer() throws Exception {
        try (val tester = getRepoTester()) {
            val consumer = new ConsumerDao();
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

    @Test
    public void getConsumer_DoestExistThrowsException() throws Exception {
        try (val tester = getRepoTester()) {
            try {
                tester.getRepository().getConsumer("aaa");
                fail("Should've failed");
            } catch (final ConsumerDoesntExistsException ex) {
                assertEquals("aaa", ex.getConsumerId());
            }
        }
    }

    @Test
    public void createConsumer_success() throws Exception {
        val consumer = new ConsumerDao();
        consumer.setConsumerId("aaa");
        consumer.getMaxExecutorCount().setValue(12345);
        try (val tester = getRepoTester()) {
            tester.getRepository().createConsumer(consumer);
            val fromDb = tester.getRepository().getConsumer("aaa");
            assertEquals(12345, (int) fromDb.getMaxExecutorCount().getValue());
        }
    }
}