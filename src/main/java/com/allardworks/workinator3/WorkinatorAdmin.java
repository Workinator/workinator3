package com.allardworks.workinator3;

import com.allardworks.workinator3.consumer.Partition;
import com.allardworks.workinator3.contracts.PartitionDto;
import com.allardworks.workinator3.contracts.PartitionOptions;
import com.allardworks.workinator3.contracts.WorkinatorAdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkinatorAdmin {
    @Autowired
    private final WorkinatorAdminRepository store;

    public Partition createPartition(final PartitionOptions partition) {
        /*val dto = new PartitionDto();
        dto.setPartitionKey(partition.getPartitionKey());
        dto.setMaxWorkerCount(partition.getMaxWorkerCount());
        dto.setMaxIdleTimeSeconds(partition.getMaxIdleTimeSeconds());*/
        // TODO: validations
        return null;
        //return store.createPartitions(partition);
    }
}
