package com.allardworks.workinator3.contracts;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode
public class Assignment {
    private final WorkerId workerId;
    private final String partitionKey;
    private final String assignmentToken;
}
