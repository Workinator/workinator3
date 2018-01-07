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
    public ConsumerRegistration registerConsumer(ConsumerId id) {
        return new ConsumerRegistration(id, "dummy");
    }

    @Override
    public void unregisterConsumer(ConsumerRegistration registration) {
        throw new NotImplementedException();
    }
}
