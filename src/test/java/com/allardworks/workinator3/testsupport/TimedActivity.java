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
    private final LocalDateTime start = LocalDateTime.now();
    private LocalDateTime stop;

    public TimedActivity stop() {
        stop = LocalDateTime.now();
        return this;
    }
    @Override
    public void close() throws Exception {
        stop();
        val elapsed = Duration.between(start, stop).toMillis();
        out.println(name + ": " + elapsed + "ms");
    }
}
