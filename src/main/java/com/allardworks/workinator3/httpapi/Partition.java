package com.allardworks.workinator3.httpapi;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Created by jaya on 3/3/18.
 * k?
 */
@Document(collection = "Partitions")
@Data
public class Partition {
    private final String partitionKey;
    private int maxIdleTimeSeconds;
    private int maxWorkerCount;
    private boolean hasWork;
    private LocalDateTime dueDate;
    private int wokerCount;
}
