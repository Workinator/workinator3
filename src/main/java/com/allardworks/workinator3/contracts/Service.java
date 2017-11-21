package com.allardworks.workinator3.contracts;

import com.allardworks.workinator3.core.Status;
import com.allardworks.workinator3.core.Transition;
import com.allardworks.workinator3.core.TransitionEvents;

import java.util.function.Consumer;

public interface Service extends AutoCloseable {
    void start();
    void stop();
    Status getStatus();
    TransitionEvents getTransitionEventHandlers();
}
