package com.allardworks.workinator3.contracts;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class Assignment {
    private final WorkerId workerId;
    private final String partitionKey;
    private final String receipt;
    private final String ruleName;
}
