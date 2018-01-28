package com.allardworks.workinator3.contracts;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class Assignment {
    private final ExecutorId executorId;
    private final String partitionKey;
    private final int workerNumber;
    private final String rule;
}
