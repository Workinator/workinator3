package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.contracts.PartitionDao;
import com.allardworks.workinator3.contracts.PartitionDoesntExistException;
import com.allardworks.workinator3.contracts.PartitionExistsException;
import com.allardworks.workinator3.contracts.WorkinatorAdminRepository;

import java.util.List;

/**
 * Created by jaya on 1/9/18.
 * k?
 */
public class DummyAdminRepository implements WorkinatorAdminRepository {
    @Override
    public void createPartitions(List<PartitionDao> partitions) throws PartitionExistsException {

    }

    @Override
    public PartitionDao createPartition(PartitionDao partition) throws PartitionExistsException {
        return null;
    }

    @Override
    public PartitionDao getPartition(String partitionKey) throws PartitionDoesntExistException {
        return null;
    }

    @Override
    public List<PartitionDao> getPartitions() {
        return null;
    }

    @Override
    public void updatePartition(PartitionDao partition) {

    }
}
