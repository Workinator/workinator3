package com.allardworks.workinator3.contracts;

public interface WorkinatorRepository {
    Assignment getAssignment(ExecutorId executorId);
    void releaseAssignment(Assignment assignment);

    void createConsumer(ConsumerDao consumer) throws ConsumerExistsException;
    ConsumerDao getConsumer(String consumerId) throws ConsumerDoesntExistsException;
    //void updateConsumer(ConsumerDao consumer) throws ConsumerExistsException;

    //ConsumerRegistration registerConsumer(ConsumerId id) throws ConsumerExistsException;
    //void unregisterConsumer(ConsumerRegistration registration);
}