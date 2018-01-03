package com.allardworks.workinator3.contracts;

import java.util.List;

public interface WorkinatorAdminRepository {
    void createPartitions(List<PartitionDto> partition) throws PartitionExistsException;
    PartitionDto createPartition(PartitionDto partition) throws PartitionExistsException;
    PartitionDto delete(PartitionDto partition);
}
