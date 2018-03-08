package com.allardworks.workinator3.demo;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jaya on 3/7/18.
 *
 * HACKETY HACK HACK
 *
 * The DEMO app hosts multiple consumersin a single process.
 * The single instance of this class is used by each of the workers.
 * It allows the demo app to set values that control the workers behavior.
 *
 * This is a wicked hack. It only works for consumers that are in process (which
 * is only a valid scenario for test purposes), and it exposes a singleton.
 * Don't do this at home. It's only for use by the demo application.
 * Workinator will eventually have a thin messaging layer for communication with workers.
 * This will go away once that is in place.
 */
public class DemoHelper {
    private final static DemoHelper hack = new DemoHelper();

    public static DemoHelper getHack() {
        return hack;
    }

    @Data
    private class DemoStuff {
        public boolean hasWork;
    }

    private final Map<String, DemoStuff> stuff = new HashMap<>();

    private DemoStuff getStuff(final String partitionKey) {
        return stuff.computeIfAbsent(partitionKey, pk -> new DemoStuff());
    }

    public DemoHelper setHasWork(final String partitionKey, boolean hasWork) {
        getStuff(partitionKey).setHasWork(hasWork);
        return this;
    }

    public boolean getHasWork(final String partitionKey){
        return getStuff(partitionKey).isHasWork();
    }

    public DemoHelper clear() {
        stuff.clear();
        return this;
    }
}
