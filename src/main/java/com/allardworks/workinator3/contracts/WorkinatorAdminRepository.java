package com.allardworks.workinator3.contracts;

import java.util.List;

public interface WorkinatorAdminRepository {
    void createPartitions(List<PartitionDao> partitions) throws PartitionExistsException;
    void createPartition(PartitionDao partition) throws PartitionExistsException;

    // TODO: parititondoesntexistexception
    void updatePartition(PartitionDao partition);
}
