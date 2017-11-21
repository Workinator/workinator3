package com.allardworks.workinator3.contracts;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.Duration;

@Builder
@Getter
public class ConsumerConfiguration {
    @NonNull
    private final String consumerName;

    @NonNull
    private final String partitionType;

    @NonNull
    private final Duration minWorkTime = Duration.ofSeconds(30);

    private final int workerCount;

    public static class ConsumerConfigurationBuilder {
        private int workerCount = 1;
    }
}