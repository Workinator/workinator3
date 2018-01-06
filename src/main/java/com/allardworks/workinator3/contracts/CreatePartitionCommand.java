package com.allardworks.workinator3.contracts;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePartitionCommand {
    private String partitionKey;
    private int maxIdleTimeSeconds;
    private int maxWorkerCount;

    public static class CreatePartitionCommandBuilder {
        private int maxIdleTimeSeconds = 30;
        private int maxWorkerCount = 1;
    }
}
