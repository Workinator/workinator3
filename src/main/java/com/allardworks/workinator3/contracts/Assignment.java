package com.allardworks.workinator3.contracts;

import com.allardworks.workinator3.consumer.Partition;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class Assignment {
    private final ExecutorId executorId;
    private final Partition partition;
    private final String rule;
    private final int partitionWorkerNumber;
}
