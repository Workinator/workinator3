package com.allardworks.workinator3.core;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.HashSet;
import java.util.List;

@Slf4j
public class EventHandlers {
    private final HashSet<Runnable> handlers = new HashSet<>();

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
        return this;
    }

    public EventHandlers remove(@NonNull Runnable runnable) {
        handlers.remove(runnable);
        return this;
    }

    public void execute() {
        for (val r : handlers) {
            try {
                r.run();
            } catch (final Exception ex) {
                log.error("Firing event handler", ex);
            }
        }
    }
}
