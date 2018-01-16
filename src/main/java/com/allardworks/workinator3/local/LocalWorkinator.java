package com.allardworks.workinator3.local;

import com.allardworks.workinator3.WorkinatorAdmin;
import com.allardworks.workinator3.contracts.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by jaya on 1/9/18.
 * k?
 */
@Service
@RequiredArgsConstructor
public class LocalWorkinator implements WorkinatorClient {
    private final WorkinatorAdmin admin;
    private final WorkinatorRepository repo;

    @Override
    public Assignment getAssignment(@NonNull final ExecutorId executorId) {
        return repo.getAssignment(executorId);
    }

    @Override
    public void releaseAssignment(@NonNull final Assignment assignment) {
        repo.releaseAssignment(assignment);
    }

    @Override
    public ConsumerRegistration registerConsumer(@NonNull final ConsumerId id) throws ConsumerExistsException {
        val dao = new ConsumerDao();
        dao.setConsumerId(id.getName());
        dao.getMaxExecutorCount().setValue(1);
        repo.createConsumer(dao);
        return new ConsumerRegistration(id);
    }

    @Override
    public void unregisterConsumer(@NonNull final ConsumerRegistration registration) {
        repo.deleteConsumer(registration.getConsumerId().getName());
    }

    @Override
    public void createPartition(@NonNull final CreatePartitionCommand command) throws PartitionExistsException {
        admin.createPartition(command);
    }
}
