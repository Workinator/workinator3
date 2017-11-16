package com.allardworks.workinator3.contracts;

import java.util.function.Consumer;

public interface Service<T> {
    void start();
    void stop();
    void onStop(Consumer<T> stopMethod);
    void onStart(Consumer<T> startMethod);
}
