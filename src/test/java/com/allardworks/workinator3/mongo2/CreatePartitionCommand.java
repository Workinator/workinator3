package com.allardworks.workinator3.mongo2;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public class CreatePartitionCommand {
    @NonNull
    private final String partitionKey;
    private final int maxWorkerCount;
    private final int maxIdleTimeSeconds;
}
