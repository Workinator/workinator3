package com.allardworks.workinator3.testsupport;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.lang.System.out;

@RequiredArgsConstructor
public class TimedActivity implements AutoCloseable {
    @NonNull
    private final String name;

    private final LocalDateTime started = LocalDateTime.now();
    private LocalDateTime stopped;
    private boolean isRunning = true;

    public TimedActivity stop() {
        stopped = LocalDateTime.now();
        isRunning = false;
        return this;
    }

    public Duration getElapsed() {
        return
                isRunning
                        ? Duration.between(started, LocalDateTime.now())
                        : Duration.between(started, stopped);
    }

    @Override
    public void close() {
        stop();
        val elapsed = Duration.between(started, stopped).toMillis();
        out.println("--------------------------" + name + ": " + elapsed + "ms");
    }
}
