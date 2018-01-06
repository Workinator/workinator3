package com.allardworks.workinator3.repository;

import com.allardworks.workinator3.contracts.PartitionDao;
import com.allardworks.workinator3.testsupport.RepositoryTester;
import lombok.val;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public abstract class AdminRepositoryTests {
    public abstract RepositoryTester getRepoTester();

    @Test
    public void createAllValues() throws Exception {
        try (val tester = getRepoTester()) {
            val partition = new PartitionDao();
            partition.setPartitionKey("abc");
            partition.getWorkCount().setValue(3000L);
            partition.getLastWork().setValue(LocalDateTime.now().minusMinutes(20));
            partition.getLastCheckStart().setValue(LocalDateTime.now().plusMinutes(20));
            partition.getMaxIdleTimeSeconds().setValue(400);
            partition.getMaxWorkerCount().setValue(3);

            /*
            val output = tester.getAdminRepository().createPartition(partition);
            assertEquals(partition.getPartitionKey(), output.getPartitionKey());
            assertEquals(partition.getMaxIdleTimeSeconds(), output.getMaxIdleTimeSeconds());
            assertEquals(partition.getMaxWorkerCount(), output.getMaxWorkerCount());
            assertEquals(partition.getLastWork(), output.getLastWork());
            assertEquals(partition.getLastCheckStart(), output.getLastCheckStart());
            assertEquals(partition.getWorkCount(), output.getWorkCount());
            */
        }
    }

    /**
     * Only partiiton key is required. don't set anything else.
     * Make sure it creates and reads.
     *
     * @throws Exception
     */
    @Test
    public void createFewestValues() throws Exception {
        try (val tester = getRepoTester()) {
            val partition = new PartitionDao();
            partition.setPartitionKey("abc");
            /*
            val output = tester.getAdminRepository().createPartition(partition);
            assertEquals(partition.getPartitionKey(), output.getPartitionKey());
            assertEquals(partition.getMaxIdleTimeSeconds(), output.getMaxIdleTimeSeconds());
            assertEquals(partition.getMaxWorkerCount(), output.getMaxWorkerCount());
            assertEquals(partition.getLastWork(), output.getLastWork());
            assertEquals(partition.getLastCheckStart(), output.getLastCheckStart());
            assertEquals(partition.getWorkCount(), output.getWorkCount());*/
        }
    }

    @Test
    public void createCantCreateDuplicate() throws Exception {
        /*
        try (val tester = getRepoTester()) {
            val partition = new PartitionDao();
            partition.setPartitionKey("abc");

            tester.getAdminRepository().createPartition(partition);
            try {
                tester.getAdminRepository().createPartition(partition);
                fail("Seconed createPartitions should've failed because the partition already exists");
            } catch (final PartitionExistsException e) {
                assertEquals(partition.getPartitionKey(), e.getPartitionKey());
            }
        }*/
    }
}
