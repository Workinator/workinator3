package com.allardworks.workinator3.httpapi;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
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
        private Date dueDate;
        private Date lastCheckedDate;
        private int workerCount;
        private final List<Worker> workers = new ArrayList<>();

        public boolean getIsOverDue() {
            return workerCount == 0 && dueDate.before(Date.from(Instant.now()));
        }
    }

    @Data
    public static class Worker {
        private String assignee;
        private String rule;
    }

    private final String partitionKey;
    private final Configuration configuration;
    private final Status status;
    private final Date createDate;
}
