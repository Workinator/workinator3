package com.allardworks.workinator3.contracts;

import com.allardworks.workinator3.consumer.Partition;

public interface WorkinatorAdminRepository {
    PartitionDto create(PartitionDto partition);
    PartitionDto update(String partitionKey, PartitionDto partition);
    PartitionDto delete(PartitionDto partition);
}
