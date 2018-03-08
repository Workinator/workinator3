package com.allardworks.workinator3.demo;

import com.allardworks.workinator3.contracts.AsyncWorker;
import com.allardworks.workinator3.contracts.WorkerContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DemoWorker implements AsyncWorker {
    @Override
    public void execute(WorkerContext context) {
        context.setHasWork(true);
    }

    @Override
    public void close() {
    }
}
