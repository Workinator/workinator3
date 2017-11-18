package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ThreadExecutorSupplier implements ExecutorSupplier {
    private final Coordinator coordinator;
    private final WorkerSupplier workerSupplier;

    @Override
    public Service create(WorkerId workerId) {
        val worker = workerSupplier.getWorker(workerId);
        return new ThreadExecutor(workerId, worker, coordinator);
    }
}
