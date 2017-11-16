package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.ConsumerConfiguration;
import com.allardworks.workinator3.contracts.Coordinator;
import com.allardworks.workinator3.core.ServiceBase;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CoordinatorConsumer extends ServiceBase  {
    @NonNull
    private final ConsumerConfiguration configuration;

    @NonNull
    private final Coordinator coordinator;

    @Override
    protected void startService(Runnable onStartComplete) {
        onStartComplete.run();
    }

    @Override
    protected void stopService(Runnable onStopComplete) {
        onStopComplete.run();
    }
}
