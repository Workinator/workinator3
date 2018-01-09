package com.allardworks.workinator3.contracts;

import java.util.List;

public interface WorkinatorAdminRepository {
    void createPartitions(List<PartitionDao> partitions) throws PartitionExistsException;
    PartitionDao createPartition(PartitionDao partition) throws PartitionExistsException;
    PartitionDao getPartition(String partitionKey) throws PartitionDoesntExistException;

    List<PartitionDao> getPartitions();

    // TODO: parititondoesntexistexception
    void updatePartition(PartitionDao partition);
}
