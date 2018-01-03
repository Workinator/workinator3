package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ExecutorFactory {
    @NonNull private final ConsumerConfiguration configuration;
    @NonNull private final WorkinatorRepository workinatorRepository;

    public Service createExecutor(@NonNull final Worker worker) {
        if (worker instanceof WorkerAsync) {
            return new ExecutorAsync(configuration, (WorkerAsync)worker, workinatorRepository);
        }

        // TODO: throw exception
        return null;
    }
}
