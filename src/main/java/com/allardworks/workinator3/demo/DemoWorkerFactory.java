package com.allardworks.workinator3.demo;

import com.allardworks.workinator3.contracts.ExecutorId;
import com.allardworks.workinator3.contracts.Worker;
import com.allardworks.workinator3.contracts.WorkerFactory;
import org.springframework.stereotype.Component;

@Component
public class DemoWorkerFactory implements WorkerFactory {
    @Override
    public Worker createWorker(ExecutorId executorId) {
        return new DemoWorker();
    }
}
