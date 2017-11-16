package com.allardworks.workinator3.contracts;

public interface Coordinator{
    Subscription getSubscription(ConsumerRegistration consumer, WorkerId workerId);
    void unsubscribe(Subscription subscription);

    ConsumerRegistration registerConsumer(ConsumerId id);
    void unregisterConsumer(ConsumerRegistration registration);
}