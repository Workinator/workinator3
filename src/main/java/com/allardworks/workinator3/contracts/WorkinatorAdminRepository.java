package com.allardworks.workinator3.contracts;

import java.util.List;
import java.util.Map;

public interface WorkinatorAdminRepository {
    void createPartitions(List<PartitionDto> partitions) throws PartitionExistsException;
    void createPartition(PartitionDto partition) throws PartitionExistsException;

    // TODO: parititondoesntexistexception
    void updatePartition(PartitionDto partition);
}
