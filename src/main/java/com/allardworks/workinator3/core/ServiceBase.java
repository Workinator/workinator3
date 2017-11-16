package com.allardworks.workinator3.core;

import com.allardworks.workinator3.contracts.Service;

import java.util.function.Consumer;

public abstract class ServiceBase<T> implements Service<T> {
    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void onStop(Consumer<T> stopMethod) {

    }

    @Override
    public void onStart(Consumer<T> startMethod) {

    }

    protected abstract void startService();
    protected abstract void stopService();
}
