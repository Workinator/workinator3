package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.contracts.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
    public ConsumerRegistration registerConsumer(ConsumerId id) throws ConsumerExistsException {
        return null;
    }

    @Override
    public void unregisterConsumer(ConsumerRegistration registration) {

    }

    @Override
    public void createPartition(CreatePartitionCommand command) throws PartitionExistsException {

    }
}
