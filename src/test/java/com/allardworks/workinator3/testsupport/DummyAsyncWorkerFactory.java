package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.contracts.Assignment;
import com.allardworks.workinator3.contracts.AsyncWorker;
import com.allardworks.workinator3.contracts.AsyncWorkerFactory;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class DummyAsyncWorkerFactory implements AsyncWorkerFactory {
    private final Supplier<AsyncWorker> supplier;

    @Override
    public AsyncWorker createWorker(Assignment assignment) {
        return supplier.get();
    }
}
