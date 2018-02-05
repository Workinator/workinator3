package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ExecutorFactory {
    @NonNull
    private final ConsumerConfiguration configuration;

    @NonNull
    private final Workinator workinatorRepository;

    /**
     * Creates an executor for the type of worker returned by the worker factory.
     * @param executorId
     * @param workerFactory used to determine what type of executor to create.
     * @return
     */
    public Service createExecutor(@NonNull ExecutorId executorId, @NonNull final WorkerFactory workerFactory) {
        if (workerFactory instanceof AsyncWorkerFactory) {
            return new ExecutorAsync(executorId, configuration, (AsyncWorkerFactory) workerFactory, workinatorRepository);
        }

        throw new RuntimeException("Unknown type of WorkerFactory. The factory must implement AsyncWorkerFactory.");
    }
}
