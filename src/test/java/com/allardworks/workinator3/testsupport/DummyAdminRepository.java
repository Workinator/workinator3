package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.commands.*;
import com.allardworks.workinator3.contracts.*;

import java.util.List;

/**
 * Created by jaya on 1/9/18.
 * k?
 */
public class DummyAdminRepository implements Workinator {
    @Override
    public Assignment getAssignment(WorkerStatus executorId) {
        return null;
    }

    @Override
    public void releaseAssignment(ReleaseAssignmentCommand command) {

    }

    @Override
    public ConsumerRegistration registerConsumer(RegisterConsumerCommand command) {
        return null;
    }


    @Override
    public void unregisterConsumer(UnregisterConsumerCommand command) {

    }

    @Override
    public void createPartition(CreatePartitionCommand command) {

    }

    @Override
    public void updateWorkerStatus(UpdateWorkersStatusCommand workerStatus) {

    }

    @Override
    public void updateConsumerStatus(UpdateConsumerStatusCommand consumerStatus) {

    }

    @Override
    public List<PartitionInfo> getPartitions() {
        return null;
    }

    @Override
    public PartitionConfiguration getPartitionConfiguration(String partitionKey) {
        return null;
    }

    @Override
    public void close() {

    }
}
