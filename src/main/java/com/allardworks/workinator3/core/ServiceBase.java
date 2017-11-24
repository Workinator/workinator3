package com.allardworks.workinator3.core;

import com.allardworks.workinator3.contracts.Service;

public abstract class ServiceBase implements Service {
    private final ServiceStatus serviceStatus = new ServiceStatus();

    public Status getStatus() {
        return serviceStatus.getStatus();
    }

    protected ServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    @Override
    public TransitionEvents getTransitionEventHandlers() {
        return serviceStatus.getEventHandlers();
    }

    @Override
    public void close() throws Exception {
        stop();
    }

    @Override
    public void start() {
        serviceStatus.starting();
    }

    @Override
    public void stop() {
        serviceStatus.stopping();
    }
}
