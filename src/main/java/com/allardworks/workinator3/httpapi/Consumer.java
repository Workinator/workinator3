package com.allardworks.workinator3.httpapi;

import lombok.Data;
import lombok.val;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Created by jaya on 3/3/18.
 * k?
 */
@Data
@Document(collection = "Consumers")
public class Consumer {
    private String name;
    private LocalDateTime connectDate;
    private int maxWorkerCount;
}
