package com.allardworks.workinator3.contracts;

import com.allardworks.workinator3.core.Transition;

import java.util.function.Consumer;

public interface Service2 extends AutoCloseable {
    void start();
    void stop();
    void onTransition(Consumer<Transition> transitionHandler);
}
