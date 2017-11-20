package com.allardworks.workinator3.core;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class EventHandlers {
    private final Set<Runnable> handlers = new HashSet<>();

    private final AtomicLong hitCount = new AtomicLong();

    public long getExecuteCount() {
        return hitCount.get();
    }

    public int size() {
        return handlers.size();
    }

    public EventHandlers add(@NonNull Runnable eventHandler) {
        handlers.add(eventHandler);
        return this;
    }

    public EventHandlers addAll(@NonNull List<Runnable> eventHandlers) {
        handlers.addAll(eventHandlers);
        return this;
    }

    public EventHandlers clear() {
        handlers.clear();
        hitCount.set(0);
        return this;
    }

    public EventHandlers remove(@NonNull Runnable runnable) {
        handlers.remove(runnable);
        return this;
    }

    public void execute() {
        val torun = handlers;
        for (val r : torun) {
            try {
                r.run();
            } catch (final Exception ex) {
                log.error("Firing event handler", ex);
            }
        }
    }
}
