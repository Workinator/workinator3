package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.contracts.AsyncWorker;
import com.allardworks.workinator3.contracts.WorkerContext;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class DummyDelegateWorker implements AsyncWorker {
    private final Consumer<WorkerContext> contextMethod;

    //@Override
    public void execute(WorkerContext context) {
        contextMethod.accept(context);
    }

    //@Override
    public void close() {

    }
}
