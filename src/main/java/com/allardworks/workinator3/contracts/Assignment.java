package com.allardworks.workinator3.contracts;

import com.allardworks.workinator3.consumer.Partition;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode
public class Assignment {
    @Getter
    private final WorkerId workerId;

    @Getter
    private final Partition partition;
}
