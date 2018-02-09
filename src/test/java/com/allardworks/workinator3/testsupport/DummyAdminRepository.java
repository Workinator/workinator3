package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.commands.CreatePartitionCommand;
import com.allardworks.workinator3.commands.RegisterConsumerCommand;
import com.allardworks.workinator3.commands.ReleaseAssignmentCommand;
import com.allardworks.workinator3.commands.UpdateWorkerStatusCommand;
import com.allardworks.workinator3.contracts.*;

/**
 * Created by jaya on 1/9/18.
 * k?
 */
public class DummyAdminRepository implements Workinator {
    @Override
    public Assignment getAssignment(WorkerStatus executorId) {
        return null;
    }

    @Override
    public void releaseAssignment(ReleaseAssignmentCommand command) {

    }

    @Override
    public ConsumerRegistration registerConsumer(RegisterConsumerCommand command) throws ConsumerExistsException {
        return null;
    }


    @Override
    public void unregisterConsumer(ConsumerRegistration registration) {

    }

    @Override
    public void createPartition(CreatePartitionCommand command) throws PartitionExistsException {

    }

    @Override
    public void updateStatus(UpdateWorkerStatusCommand workerStatus) {

    }

    @Override
    public void close() throws Exception {

    }
}
