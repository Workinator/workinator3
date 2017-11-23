package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.security.auth.login.Configuration;

@RequiredArgsConstructor
public class ExecutorFactory {
    @NonNull private final ConsumerConfiguration configuration;
    @NonNull private final Coordinator coordinator;

    public Service createExecutor(@NonNull final Worker worker) {
        if (worker instanceof WorkerAsync) {
            return new ExecutorAsync(configuration, worker, coordinator);
        }

        // TODO: throw exception
        return null;
    }
}
