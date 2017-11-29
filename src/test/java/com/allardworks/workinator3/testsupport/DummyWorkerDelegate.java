package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.contracts.WorkerAsync;
import com.allardworks.workinator3.contracts.WorkerContext;
import com.allardworks.workinator3.contracts.ExecutorId;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class DummyWorkerDelegate implements WorkerAsync {
    private final Consumer<WorkerContext> contextMethod;

    //@Override
    public void execute(WorkerContext context) {
        contextMethod.accept(context);
    }

    //@Override
    public void close() throws Exception {

    }

    @Override
    public ExecutorId getId() {
        return null;
    }
}
