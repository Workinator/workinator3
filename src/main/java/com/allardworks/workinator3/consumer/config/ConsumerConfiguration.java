package com.allardworks.workinator3.consumer.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static lombok.AccessLevel.PRIVATE;

/**
 * DURATION isn't working with constructors.
 * Binding doesn't work for private setters.
 * So, this class isn't how I would like it.
 * I want to make these properties FINAL.
 */

@Data
@Configuration
@ConfigurationProperties("consumer")
@Builder
public class ConsumerConfiguration {
    /**
     * The minimum amount of time that a worker will work
     * without interruption, as long as it has work.
     */
    @NonNull
    private Duration minWorkTime;

    /**
     * If there isn't an assignment for a worker,
     * then delay before checking agian.
     */
    @NonNull
    private Duration delayWhenNoAssignment;

    /**
     * The maximum number of workers the consumer
     * can process at once.
     */
    private int maxWorkerCount;

    public static class ConsumerConfigurationBuilder {
        private int maxWorkerCount = 1;
        private Duration minWorkTime = Duration.ofSeconds(5);
        private Duration delayWhenNoAssignment = Duration.ofSeconds(5);
    }
}