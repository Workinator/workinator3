package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.contracts.WorkerAsync;
import com.allardworks.workinator3.contracts.WorkerId;
import com.allardworks.workinator3.contracts.WorkerSupplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class DummyWorkerSupplier implements WorkerSupplier {
    private final Supplier<WorkerAsync> supplier;

    @Override
    public WorkerAsync getWorker(@NonNull final WorkerId workerId) {
        return supplier.get();
    }
}
