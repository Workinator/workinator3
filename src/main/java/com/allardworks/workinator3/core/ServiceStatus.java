package com.allardworks.workinator3.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceStatus {
    private final Object lock = new Object();

    @Getter
    private Status status = Status.Stopped;

    public void start(final Runnable startMethod) {
        synchronized (lock) {
            if (!status.isStopped()) {
                return;
            }

            status = Status.Starting;
            try {
                startMethod.run();
            } catch (final Exception ex) {
                log.error("ServiceStatus.start() failed", ex);
            }
        }
    }

    public void stop(final Runnable stopMethod) {
        synchronized (lock) {
            if (!status.isStarted()) {
                return;
            }

            status = Status.Stopping;
            try {
                stopMethod.run();
            } catch (final Exception ex) {
                log.error("ServiceStatus.stop() failed", ex);
            }
        }
    }

    public void startComplete() {
        synchronized (lock) {
            if (status.equals(Status.Starting)) {
                status = Status.Started;
            }
        }
    }

    public void stopComplete() {
        synchronized (lock) {
            if (status.equals(Status.Stopping)) {
                status = Status.Stopped;
            }
        }
    }
}
