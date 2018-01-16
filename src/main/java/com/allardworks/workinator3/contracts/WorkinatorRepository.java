package com.allardworks.workinator3.contracts;

public interface WorkinatorRepository {
    Assignment getAssignment(ExecutorId executorId);
    void releaseAssignment(Assignment assignment);

    void createConsumer(ConsumerDao consumer) throws ConsumerExistsException;
    ConsumerDao getConsumer(String consumerId) throws ConsumerDoesntExistsException;
    void deleteConsumer(String consumerId);
}