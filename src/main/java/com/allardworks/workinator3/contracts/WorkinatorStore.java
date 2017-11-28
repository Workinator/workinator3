package com.allardworks.workinator3.contracts;

public interface WorkinatorStore {
    Assignment getAssignment(WorkerId workerId);
    void releaseAssignment(Assignment assignment);

    ConsumerRegistration registerConsumer(ConsumerId id);
    void unregisterConsumer(ConsumerRegistration registration);
}