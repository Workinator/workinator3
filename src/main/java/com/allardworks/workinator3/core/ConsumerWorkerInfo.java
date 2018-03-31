package com.allardworks.workinator3.core;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class ConsumerWorkerInfo {
    private final int workerNumber;
    private final Date assignmentDate;
    private final String partitionKey;
    private final String rule;
}
