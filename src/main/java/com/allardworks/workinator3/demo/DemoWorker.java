package com.allardworks.workinator3.demo;

import com.allardworks.workinator3.contracts.ExecutorId;
import com.allardworks.workinator3.contracts.WorkerAsync;
import com.allardworks.workinator3.contracts.WorkerContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DemoWorker implements WorkerAsync {
    private final ExecutorId id;

    @Override
    public void execute(WorkerContext context) {
    }

    @Override
    public ExecutorId getId() {
        return id;
    }

    @Override
    public void close() throws Exception {
    }
}
