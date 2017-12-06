package com.allardworks.workinator3.mongo;

import lombok.val;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MongoConfigurationTests {
    @Test
    public void defaults() {
        val config = MongoConfiguration
                .builder()
                .partitionType("xyz")
                .build();
        assertEquals("Workinator", config.getDatabaseName());
        assertEquals("Partitions_xyz", config.getPartitionsCollectionName());
        assertEquals("Workers_xyz", config.getWorkersCollectionName());
        assertEquals("localhost", config.getHost());
        assertEquals(27017, config.getPort());
    }
}
