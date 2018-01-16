package com.allardworks.workinator3.local;

import com.allardworks.workinator3.WorkinatorAdmin;
import com.allardworks.workinator3.contracts.*;
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
    public Assignment getAssignment(final ExecutorId executorId) {
        return repo.getAssignment(executorId);
    }

    @Override
    public void releaseAssignment(final Assignment assignment) {
        repo.releaseAssignment(assignment);
    }

    @Override
    public ConsumerRegistration registerConsumer(final ConsumerId id) throws ConsumerExistsException {
        val dao = new ConsumerDao();
        dao.setConsumerId(id.getName());
        dao.setConsumerRegistration(UUID.randomUUID().toString());
        dao.getMaxExecutorCount().setValue(1);
        repo.createConsumer(dao);
        return new ConsumerRegistration(id, dao.getConsumerRegistration());
    }

    @Override
    public void unregisterConsumer(final ConsumerRegistration registration) {

        //repo.unregisterConsumer(registration);
    }

    @Override
    public void createPartition(final CreatePartitionCommand command) throws PartitionExistsException {
        admin.createPartition(command);
    }
}
