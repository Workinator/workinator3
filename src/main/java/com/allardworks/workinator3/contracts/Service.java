package com.allardworks.workinator3.contracts;

import com.allardworks.workinator3.core.Status;
import com.allardworks.workinator3.core.TransitionEvents;

import java.util.Map;

public interface Service extends AutoCloseable {
    void start();
    void stop();
    Status getStatus();
    TransitionEvents getTransitionEventHandlers();
    Map<String, Object> getInfo();
}
