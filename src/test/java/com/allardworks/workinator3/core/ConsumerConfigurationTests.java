package com.allardworks.workinator3.core;

import com.allardworks.workinator3.contracts.ConsumerConfiguration;
import lombok.val;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ConsumerConfigurationTests {
    @Test
    public void workerCountDefaultsTo1() {
        val config = ConsumerConfiguration
                .builder()
                .build();
        assertEquals(1, config.getMaxWorkerCount());
    }

    @Test
    public void workerCountSetter() {
        val config = ConsumerConfiguration
                .builder()
                .maxWorkerCount(10)
                .build();
        assertEquals(10, config.getMaxWorkerCount());
    }

    @Test
    public void noSetters() {
        val setterCount =
                Arrays.stream(
                ConsumerConfiguration
                .class
                .getMethods()).filter(m -> m.getName().startsWith("set"))
                .count();
        assertEquals(0, setterCount);
    }
}
