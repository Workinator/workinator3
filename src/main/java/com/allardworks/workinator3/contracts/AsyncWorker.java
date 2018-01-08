package com.allardworks.workinator3.contracts;

public interface AsyncWorker extends Worker {
    void execute(WorkerContext context);
}
