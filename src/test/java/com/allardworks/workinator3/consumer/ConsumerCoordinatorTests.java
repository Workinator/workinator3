package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.ConsumerConfiguration;
import lombok.val;
import org.assertj.core.util.Preconditions;
import org.junit.Test;

import static java.lang.System.out;

public class ConsumerCoordinatorTests {
    @Test
    public void blah() throws Exception {
        val config = ConsumerConfiguration
                .builder()
                .partitionType("test")
                .build();

        //try(val z = new CoordinatorConsumer(ConsumerConfiguration.builder().build(), null, null).start()) {
        //    out.println(z);
        //}
    }
}

