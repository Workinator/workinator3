package com.allardworks.workinator3.contracts;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public class PartitionOptions {
    @NonNull
    private final String partitionKey;
    private final int maxIdleTimeSeconds;
    private final int maxWorkerCount;

    public static class PartitionOptionsBuilder {
        private int maxIdleSeconds = 30;
        private int maxWorkerCount = 1;
    }
}
