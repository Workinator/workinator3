package com.allardworks.workinator3.contracts;

public interface WorkinatorRepository {
    Assignment getAssignment(ExecutorId executorId);
    void releaseAssignment(Assignment assignment);

    ConsumerRegistration registerConsumer(ConsumerId id) throws ConsumerExistsException;
    void unregisterConsumer(ConsumerRegistration registration);
}