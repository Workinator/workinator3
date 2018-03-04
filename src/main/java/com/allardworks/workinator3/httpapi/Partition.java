package com.allardworks.workinator3.httpapi;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaya on 3/3/18.
 * k?
 */
@Document(collection = "Partitions")
@Data
public class Partition {
    @Data
    public static class Configuration{
        private int maxIdleTimeSeconds;
        private int maxWorkerCount;
    }

    @Data
    public static class Status {
        private boolean hasWork;
        private LocalDateTime dueDate;
        private int workerCount;
        private final List<Worker> workers = new ArrayList<>();
    }

    @Data
    public static class Worker {
        private String assignee;
        private String rule;
    }


    private final String partitionKey;
    private final Configuration configuration;
    private final Status status;
}
