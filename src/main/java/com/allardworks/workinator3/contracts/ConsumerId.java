package com.allardworks.workinator3.contracts;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor
public class ConsumerId {
    private final String name;
    private final String partitionType;
}
