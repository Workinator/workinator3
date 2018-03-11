package com.allardworks.workinator3.httpapi;

import com.allardworks.workinator3.mongo2.MongoWorkinator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.allardworks.workinator3.httpapi.JsonUtility.json;

/**
 * Created by jaya on 3/10/18.
 * k?
 */
@Configuration
public class ServiceConfig {
    @Bean
    public ObjectMapper getObjectMapper() {
        return json();
    }
}
