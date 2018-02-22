package com.allardworks.workinator3.contracts;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class PartitionInfo {
    private final String partitionKey;
    private final int maxWorkerCount;
    private final int maxIdleTimeSeconds;
    private final boolean hasMoreWork;
    private final LocalDateTime lastChecked;
    private final int currentWorkerCount;
    private final List<WorkerInfo> workers;
}
