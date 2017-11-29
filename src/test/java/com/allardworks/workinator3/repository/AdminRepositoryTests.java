package com.allardworks.workinator3.repository;

import com.allardworks.workinator3.contracts.PartitionDto;
import com.allardworks.workinator3.contracts.PartitionExistsException;
import com.allardworks.workinator3.testsupport.RepositoryTester;
import lombok.val;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public abstract class AdminRepositoryTests {
    public abstract RepositoryTester getRepoTester();

    @Test
    public void createAllValues() throws Exception {
        try (val tester = getRepoTester()) {
            val partition = new PartitionDto();
            partition.setPartitionKey("abc");
            partition.setWorkCount(3000);
            partition.setLastWork(LocalDateTime.now().minusMinutes(20));
            partition.setLastCheckStart(LocalDateTime.now().plusMinutes(20));
            partition.setMaxIdleTimeSeconds(400);
            partition.setMaxWorkerCount(3);

            val output = tester.getAdminRepository().create(partition);
            assertEquals(partition.getPartitionKey(), output.getPartitionKey());
            assertEquals(partition.getMaxIdleTimeSeconds(), output.getMaxIdleTimeSeconds());
            assertEquals(partition.getMaxWorkerCount(), output.getMaxWorkerCount());
            assertEquals(partition.getLastWork(), output.getLastWork());
            assertEquals(partition.getLastCheckStart(), output.getLastCheckStart());
            assertEquals(partition.getWorkCount(), output.getWorkCount());
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
            val partition = new PartitionDto();
            partition.setPartitionKey("abc");

            val output = tester.getAdminRepository().create(partition);
            assertEquals(partition.getPartitionKey(), output.getPartitionKey());
            assertEquals(partition.getMaxIdleTimeSeconds(), output.getMaxIdleTimeSeconds());
            assertEquals(partition.getMaxWorkerCount(), output.getMaxWorkerCount());
            assertEquals(partition.getLastWork(), output.getLastWork());
            assertEquals(partition.getLastCheckStart(), output.getLastCheckStart());
            assertEquals(partition.getWorkCount(), output.getWorkCount());
        }
    }

    @Test
    public void createCantCreateDuplicate() throws Exception {
        try (val tester = getRepoTester()) {
            val partition = new PartitionDto();
            partition.setPartitionKey("abc");

            tester.getAdminRepository().create(partition);
            try {
                tester.getAdminRepository().create(partition);
                fail("Seconed create should've failed because the partition already exists");
            } catch (final PartitionExistsException e) {
                assertEquals(partition.getPartitionKey(), e.getPartitionKey());
            }
        }
    }
}
