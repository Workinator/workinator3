package com.allardworks.workinator3.contracts;

import java.util.function.Consumer;

public interface Service extends AutoCloseable {
    Service start();
    Service stop();
    Service onStopped(Consumer<Service> sender);
    Service onStarted(Consumer<Service> sender);
}
