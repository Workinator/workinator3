package com.allardworks.workinator3.demo;

import com.allardworks.workinator3.contracts.ConsumerConfiguration;
import com.allardworks.workinator3.mongo2.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    public MongoConfiguration getMongoConfiguration() {
        return MongoConfiguration.builder().build();
    }

    @Bean
    public ConsumerConfiguration getConsumerConfiguration() {
        return ConsumerConfiguration.builder().build();
    }

    @Autowired
    @Bean
    public AssignmentStrategy getAssignmentStrategy(final MongoDal dal, final PartitionConfigurationCache cache) {
        return new WhatsNextAssignmentStrategy(dal, cache);
    }
}
