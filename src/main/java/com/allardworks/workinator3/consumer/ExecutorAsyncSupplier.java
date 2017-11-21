package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExecutorAsyncSupplier implements ExecutorSupplier {
    @NonNull private final ConsumerConfiguration configuration;
    @NonNull private final Coordinator coordinator;

    @Override
    public Service create(@NonNull final Worker worker) {
        return new ExecutorAsync(configuration, worker, coordinator);
    }
}
