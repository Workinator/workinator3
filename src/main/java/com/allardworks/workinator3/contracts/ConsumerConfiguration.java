package com.allardworks.workinator3.contracts;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;

@Builder
@Getter
public class ConsumerConfiguration {
    private final String consumerName;
    private final String partitionType;
    private final int workerCount;
    private final Duration minWorkTime;
}
