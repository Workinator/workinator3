package com.allardworks.workinator3.core;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Builder
@Data
public class PartitionInfo {
    private final String partitionKey;
    private final int maxWorkerCount;
    private final int maxIdleTimeSeconds;
    private final boolean hasMoreWork;
    private final Date lastChecked;
    private final int currentWorkerCount;
    private final List<WorkerInfo> workers;
}
