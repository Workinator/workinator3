package com.allardworks.workinator3.demo;

import com.allardworks.workinator3.contracts.ExecutorId;
import com.allardworks.workinator3.contracts.WorkerAsync;
import com.allardworks.workinator3.contracts.WorkerContext;
import lombok.RequiredArgsConstructor;

import static java.lang.System.out;

@RequiredArgsConstructor
public class DemoWorker implements WorkerAsync {
    private final ExecutorId id;

    @Override
    public void execute(WorkerContext context) {
        //out.println(context.getAssignment().getPartitionWorkerNumber());

    }

    @Override
    public void close() throws Exception {
    }
}
