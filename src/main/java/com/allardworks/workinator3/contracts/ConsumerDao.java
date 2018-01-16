package com.allardworks.workinator3.contracts;

import com.allardworks.workinator3.core.NullableOptional;
import lombok.Data;

@Data
public class ConsumerDao {
    private String consumerId;
    private String consumerRegistration;
    private final NullableOptional<Integer> maxExecutorCount = new NullableOptional<>();
}