package com.allardworks.workinator3.contracts;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.Duration;

@Builder
@Getter
public class ConsumerConfiguration {
    @NonNull private final String consumerName;
    @NonNull private final String partitionType;
    @NonNull @Builder.Default private final int workerCount = 1;
    @NonNull private final Duration minWorkTime = Duration.ofSeconds(30);
}
