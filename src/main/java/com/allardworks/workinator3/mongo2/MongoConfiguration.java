package com.allardworks.workinator3.mongo2;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Getter
@Builder
public class MongoConfiguration {
    private final String host;
    private final int port;
    private final String databaseName;

    public String getPartitionsCollectionName() {
        return "Partitions";
    }

    public String getConsumersCollectionName() {
        return "Consumers";
    }

    public static class MongoConfigurationBuilder {
        private String databaseName = "Workinator";
        private String host = "localhost";
        private int port = 27017;
    }
}
