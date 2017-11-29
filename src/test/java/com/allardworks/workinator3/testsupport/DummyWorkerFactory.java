package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.contracts.WorkerAsync;
import com.allardworks.workinator3.contracts.ExecutorId;
import com.allardworks.workinator3.contracts.WorkerFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class DummyWorkerFactory implements WorkerFactory {
    private final Supplier<WorkerAsync> supplier;

    @Override
    public WorkerAsync createWorker(@NonNull final ExecutorId executorId) {
        return supplier.get();
    }
}