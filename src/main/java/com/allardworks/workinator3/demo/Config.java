package com.allardworks.workinator3.demo;

import com.allardworks.workinator3.contracts.ConsumerConfiguration;
import com.allardworks.workinator3.mongo2.AssignmentStrategy;
import com.allardworks.workinator3.mongo2.MongoConfiguration;
import com.allardworks.workinator3.mongo2.MongoDal;
import com.allardworks.workinator3.mongo2.WhatsNextAssignmentStrategy;
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

    @Bean
    public AssignmentStrategy getAssignmentStrategy(@Autowired final MongoDal dal) {
        return new WhatsNextAssignmentStrategy(dal);
    }
}
