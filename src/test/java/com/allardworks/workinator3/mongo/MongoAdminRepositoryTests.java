package com.allardworks.workinator3.mongo;

import com.allardworks.workinator3.contracts.PartitionDto;
import lombok.val;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class MongoAdminRepositoryTests {
    @Test
    public void insertOne() {
        val config = new MongoConfiguration("yadda", "localhost", 27017, "test-" + System.currentTimeMillis());
        val repo = new MongoAdminRepository(config);
        val partition = new PartitionDto();
        partition.setPartitionKey("abc");
        partition.setWorkCount(3000);
        partition.setLastWork(LocalDateTime.now().minusMinutes(20));
        partition.setLastCheck(LocalDateTime.now().plusMinutes(20));
        partition.setSynchronizationKey(UUID.randomUUID());
        partition.setMaxIdleTimeSeconds(400);
        partition.setMaxWorkerCount(3);

        val output = repo.create(partition);
        assertEquals(partition.getPartitionKey(), output.getPartitionKey());
        assertEquals(partition.getMaxIdleTimeSeconds(), output.getMaxIdleTimeSeconds());
        assertEquals(partition.getMaxWorkerCount(), output.getMaxWorkerCount());
        assertEquals(partition.getLastWork(), output.getLastWork());
        assertEquals(partition.getLastCheck(), output.getLastCheck());
        assertEquals(partition.getSynchronizationKey(), output.getSynchronizationKey());
        assertEquals(partition.getWorkCount(), output.getWorkCount());
    }
}
