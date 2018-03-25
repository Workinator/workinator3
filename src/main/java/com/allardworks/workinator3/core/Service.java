package com.allardworks.workinator3.core;

import java.util.Map;

public interface Service extends AutoCloseable {
    void start();
    void stop();
    Status getStatus();
    TransitionEvents getTransitionEventHandlers();
    Map<String, Object> getInfo();
}
