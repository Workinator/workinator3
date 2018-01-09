package com.allardworks.workinator3.repository;

import com.allardworks.workinator3.contracts.PartitionDao;
import com.allardworks.workinator3.contracts.PartitionExistsException;
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

            val output = tester.getAdminRepository().createPartition(partition);
            assertEquals(partition.getPartitionKey(), output.getPartitionKey());
            assertEquals(partition.getMaxIdleTimeSeconds().getValue(), output.getMaxIdleTimeSeconds().getValue());
            assertEquals(partition.getMaxWorkerCount().getValue(), output.getMaxWorkerCount().getValue());
            assertEquals(partition.getLastWork().getValue(), output.getLastWork().getValue());
            assertEquals(partition.getLastCheckStart().getValue(), output.getLastCheckStart().getValue());
            assertEquals(partition.getWorkCount().getValue(), output.getWorkCount().getValue());
        }
    }

    @Test
    public void createCantCreateDuplicatePartition() throws Exception {
        try (val tester = getRepoTester()) {
            val partition = new PartitionDao();
            partition.setPartitionKey("abc");
            partition.getMaxWorkerCount().setValue(5);

            tester.getAdminRepository().createPartition(partition);
            try {
                tester.getAdminRepository().createPartition(partition);
                fail("Seconed createPartitions should've failed because the partition already exists");
            } catch (final PartitionExistsException e) {
                assertEquals(partition.getPartitionKey(), e.getPartitionKey());
            }
        }
    }
}
