package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.contracts.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class DummyWorkerFactory implements AsyncWorkerFactory {
    private final Supplier<AsyncWorker> supplier;

    @Override
    public AsyncWorker createWorker(Assignment assignment) {
        return supplier.get();
    }
}
