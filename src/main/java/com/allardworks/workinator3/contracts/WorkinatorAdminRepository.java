package com.allardworks.workinator3.contracts;

import java.util.List;

public interface WorkinatorAdminRepository {
    void create(List<PartitionDto> partition) throws PartitionExistsException;
    PartitionDto create(PartitionDto partition) throws PartitionExistsException;
    PartitionDto update(PartitionDto partition);
    PartitionDto delete(PartitionDto partition);
}
