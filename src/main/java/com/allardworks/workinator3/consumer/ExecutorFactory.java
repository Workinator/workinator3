package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExecutorFactory {
    @NonNull private final ConsumerConfiguration configuration;
    @NonNull private final WorkinatorRepository workinatorRepository;

    public Service createExecutor(@NonNull final Worker worker) {
        if (worker instanceof WorkerAsync) {
            return new ExecutorAsync(configuration, worker, workinatorRepository);
        }

        // TODO: throw exception
        return null;
    }
}
