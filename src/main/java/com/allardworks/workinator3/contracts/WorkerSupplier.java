package com.allardworks.workinator3.contracts;

public interface WorkerSupplier {
    Worker getWorker(WorkerId workerId);
}
