package com.allardworks.workinator3.httpapi;

import lombok.Data;

/**
 * Created by jaya on 3/6/18.
 * k?
 */
@Data
public class Stats {
    @Data
    private static class PartitionStats {
        private int partitionCount;
        private int overduePartitionCount;

    }

    @Data
    private static class ConsumerStats {
        private int consumerCount;
        private int workerCount;
        private int busyWorkerCount;
    }

    private PartitionStats partitionStats;
    private ConsumerStats consumerStats;
}
