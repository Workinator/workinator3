package com.allardworks.workinator3.httpapi;

import com.allardworks.workinator3.contracts.ConsumerRegistration;
import com.allardworks.workinator3.contracts.ConsumerStatus;
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
    // TODO: matches what's in the db, but the db needs some work. status/status needs to go.
    @Data
    private static class Status {
        private ConsumerRegistration registration;
        private ConsumerStatus status;
    }

    private Status status;
    private String name;
    private LocalDateTime connectDate;
    private int maxWorkerCount;
}
