package com.allardworks.workinator3.contracts;

public interface WorkerAsync extends Worker {
    void execute(WorkerContext context);
}
