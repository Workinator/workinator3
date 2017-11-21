package com.allardworks.workinator3.contracts;

public interface WorkerSupplier {
    WorkerAsync getWorker(WorkerId workerId);
}
