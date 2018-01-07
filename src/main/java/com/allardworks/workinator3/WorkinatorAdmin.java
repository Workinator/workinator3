package com.allardworks.workinator3;

import com.allardworks.workinator3.consumer.Partition;
import com.allardworks.workinator3.contracts.CreatePartitionCommand;
import com.allardworks.workinator3.contracts.PartitionDao;
import com.allardworks.workinator3.contracts.PartitionExistsException;
import com.allardworks.workinator3.contracts.WorkinatorAdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.allardworks.workinator3.core.ConvertUtility.MinDate;

@Service
@RequiredArgsConstructor
public class WorkinatorAdmin {
    @Autowired
    private final WorkinatorAdminRepository store;

    public void createPartition(final CreatePartitionCommand command) throws PartitionExistsException {
        val dao = new PartitionDao();

        // from command
        dao.setPartitionKey(command.getPartitionKey());
        dao.getMaxWorkerCount().setValue(command.getMaxWorkerCount());
        dao.getMaxIdleTimeSeconds().setValue(command.getMaxIdleTimeSeconds());

        // defaults
        dao.getLastCheckStart().setValue(MinDate);
        dao.getLastCheckEnd().setValue(MinDate);
        dao.getLastWork().setValue(MinDate);
        dao.getWorkCount().setValue(0L);
        dao.getHasMoreWork().setValue(false);
        store.createPartition(dao);
    }

    public List<PartitionDao> getPartitions() {
        return store.getPartitions();
    }
}
