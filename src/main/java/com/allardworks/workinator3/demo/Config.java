package com.allardworks.workinator3.demo;

import com.allardworks.workinator3.contracts.ConsumerConfiguration;
import com.allardworks.workinator3.mongo2.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Config {
    @Bean
    public MongoConfiguration getMongoConfiguration() {
        return MongoConfiguration.builder().build();
    }

    // TODO: i tried using duration, but didn't work. figure  it out.
    @Bean
    public ConsumerConfiguration getConsumerConfiguration(
            @Value("${consumer.maxWorkerCount}") int maxWorkerCount,
            @Value("${consumer.minWorkTime}") String minWorkTimeSeconds,
            @Value("${consumer.delayWhenNoAssignment}") String delayWhenNoAssignmentSeconds

    ) {
        return ConsumerConfiguration
                .builder()
                .maxWorkerCount(maxWorkerCount)
                .minWorkTime(Duration.ofSeconds(Long.parseLong(minWorkTimeSeconds)))
                .delayWhenNoAssignment(Duration.ofSeconds(Integer.parseInt(minWorkTimeSeconds)))
                .build();
    }

    @Autowired
    @Bean
    public AssignmentStrategy getAssignmentStrategy(final MongoDal dal, final PartitionConfigurationCache cache) {
        return new WhatsNextAssignmentStrategy(dal, cache);
    }
}
