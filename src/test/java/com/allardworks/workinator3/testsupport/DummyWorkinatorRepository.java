package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.contracts.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class DummyWorkinatorRepository implements WorkinatorRepository {
    private Assignment nextAssignment;

    public void setNextAssignment(Assignment assignment) {
        nextAssignment = assignment;
    }

    @Override
    public Assignment getAssignment(ExecutorId executorId) {
        return nextAssignment;
    }

    @Override
    public void releaseAssignment(Assignment assignment) {

    }

    @Override
    public void createConsumer(ConsumerDao consumer) throws ConsumerExistsException {

    }

    @Override
    public ConsumerDao getConsumer(String consumerId) throws ConsumerDoesntExistsException {
        return null;
    }
}
