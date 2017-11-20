package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ThreadExecutorSupplier implements ExecutorSupplier {
    private final Coordinator coordinator;
    private final WorkerSupplier workerSupplier;
    private final ConsumerConfiguration configuration;

    @Override
    public Service2 create(WorkerId workerId) {
        val worker = workerSupplier.getWorker(workerId);
        return new ThreadExecutor(configuration, workerId, worker, coordinator);
    }
}
