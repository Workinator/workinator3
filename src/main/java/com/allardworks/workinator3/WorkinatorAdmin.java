package com.allardworks.workinator3;

import com.allardworks.workinator3.consumer.Partition;
import com.allardworks.workinator3.contracts.WorkinatorAdminRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorkinatorAdmin {
    private final WorkinatorAdminRepository store;

    public Partition createPartition(final Partition partition) {
        // TODO: validations
        return null;
        //return store.create(partition);
    }
}
