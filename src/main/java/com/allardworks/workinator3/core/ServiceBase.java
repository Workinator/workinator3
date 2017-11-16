package com.allardworks.workinator3.core;

import com.allardworks.workinator3.contracts.Service;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public abstract class ServiceBase implements Service {
    private final ServiceStatus status = new ServiceStatus();
    private final List<Consumer<Service>> startEvents = new ArrayList<>();
    private final List<Consumer<Service>> stopEvents = new ArrayList<>();

    @Override
    public Service start() {
        status.start(() -> {
            status.startComplete();
            executeHandlers(startEvents);
        });
        return this;
    }

    @Override
    public Service stop() {
        status.stop(() -> {
            status.stopComplete();
            executeHandlers(stopEvents);
        });
        return this;
    }

    @Override
    public Service onStopped(@NonNull Consumer<Service> sender) {
        stopEvents.add(sender);
        return this;
    }

    @Override
    public Service onStarted(@NonNull Consumer<Service> sender) {
        startEvents.add(sender);
        return this;
    }

    @Override
    public void close() {
    }

    protected abstract void startService(Runnable onStartComplete);

    protected abstract void stopService(Runnable onStopComplete);

    private void executeHandlers(List<Consumer<Service>> events) {
        // TODO: clone events list
        for (val evt : events) {
            try {
                evt.accept(this);
            } catch (final Exception e) {
                log.error("Error executing event handler", e);
            }
        }
    }
}
