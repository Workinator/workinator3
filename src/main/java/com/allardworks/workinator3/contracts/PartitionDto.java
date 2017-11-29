package com.allardworks.workinator3.contracts;

import com.allardworks.workinator3.core.ConvertUtility;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class PartitionDto {
    private String partitionKey;
    private LocalDateTime lastWork;
    private LocalDateTime lastCheckStart;
    private LocalDateTime lastCheckEnd = ConvertUtility.MinDate;
    private boolean hasMoreWork;
    private long workCount;
    private int maxIdleTimeSeconds = 30;
    private int maxWorkerCount = 1;

}
