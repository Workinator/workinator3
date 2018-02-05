package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.commands.CreatePartitionCommand;
import com.allardworks.workinator3.commands.RegisterConsumerCommand;
import com.allardworks.workinator3.contracts.*;

public class DummyWorkinator implements Workinator {
    private Assignment next;

    public void setNextAssignment(final Assignment assignment) {
        next = assignment;
    }

    @Override
    public Assignment getAssignment(ExecutorStatus executorId) {
        return next;
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
