package com.allardworks.workinator3.contracts;

public interface Coordinator{
    Assignment getAssignment(WorkerId workerId);
    void releaseAssignment(Assignment assignment);

    ConsumerRegistration registerConsumer(ConsumerId id);
    void unregisterConsumer(ConsumerRegistration registration);
}