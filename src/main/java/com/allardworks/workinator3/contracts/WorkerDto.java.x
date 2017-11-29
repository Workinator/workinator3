package com.allardworks.workinator3.contracts;

import lombok.Data;

import java.time.LocalTime;

@Data
public class WorkerDto {
    private final String partitionKey;
    private final int workerNumber;
    private LocalTime lockDate;
    private String currentAssignee;
}
