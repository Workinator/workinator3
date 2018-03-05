package com.allardworks.workinator3.contracts;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.Duration;

@Builder
@Getter
public class ConsumerConfiguration {
    /**
     * The minimum amount of time that a worker will work
     * without interruption, as long as it has work.
     */
    @NonNull
    private final Duration minWorkTime;

    /**
     * If there isn't an assignment for a worker,
     * then delay before checking agian.
     */
    @NonNull
    private final Duration delayWhenNoAssignment;

    /**
     * The maximum number of workers the consumer
     * can process at once.
     */
    private final int maxWorkerCount;

    public static class ConsumerConfigurationBuilder {
        private int maxWorkerCount = 1;
        private Duration getMinWorkTime = Duration.ofSeconds(5);
        private Duration delayWhenNoAssignment = Duration.ofSeconds(5);
    }
}