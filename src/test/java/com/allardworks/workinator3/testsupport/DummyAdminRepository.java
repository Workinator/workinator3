package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.commands.CreatePartitionCommand;
import com.allardworks.workinator3.commands.RegisterConsumerCommand;
import com.allardworks.workinator3.contracts.*;

/**
 * Created by jaya on 1/9/18.
 * k?
 */
public class DummyAdminRepository implements Workinator {
    @Override
    public Assignment getAssignment(ExecutorStatus executorId) {
        return null;
    }

    @Override
    public void releaseAssignment(Assignment assignment) {

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
    public void close() throws Exception {

    }
}
