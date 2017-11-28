package com.allardworks.workinator3.mongo;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public class MongoConfiguration {
    @NonNull
    private final String partitionType;
    @NonNull
    private final String host;
    private final int port;
    private final String databaseName;

    public String getCollectionName() {
        return "Partitions_" + partitionType;
    }

    public static class WorkinatorMongoConfigurationBuilder {
        private String databaseName = "Workinator";
    }
}
