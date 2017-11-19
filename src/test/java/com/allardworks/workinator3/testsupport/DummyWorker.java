package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.contracts.Worker;
import com.allardworks.workinator3.contracts.WorkerContext;
import lombok.Data;

@Data
public class DummyWorker implements Worker {
    private WorkerContext lastContext;

    @Override
    public void execute(WorkerContext context) {
        lastContext = context;
    }
}
