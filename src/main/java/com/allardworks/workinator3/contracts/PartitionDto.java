package com.allardworks.workinator3.contracts;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class PartitionDto {
    private UUID synchronizationKey;
    private String partitionKey;
    private LocalDateTime lastWork;
    private LocalDateTime lastCheck;
    private boolean hasMoreWork;
    private long workCount;
    private int maxIdleTimeSeconds = 30;
    private int maxWorkerCount = 1;

}
