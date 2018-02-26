package com.allardworks.workinator3.contracts;

import lombok.Builder;
import lombok.Data;

/**
 * Created by jaya on 2/25/18.
 * k?
 */
@Data
@Builder
public class PartitionConfiguration {
    private final String partitionKey;
    private final int maxWorkerCount;
    private final int maxIdleTimeSeconds;
}
