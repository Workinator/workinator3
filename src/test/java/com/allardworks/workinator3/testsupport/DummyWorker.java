package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.contracts.Worker;
import com.allardworks.workinator3.contracts.WorkerContext;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.out;

@Data
public class DummyWorker implements Worker {
    private WorkerContext lastContext;
    private long hitCount = 0;
    private boolean isFrozen;
    private boolean thawOnStop = false;
    private final AtomicInteger contextStopEventHitCount = new AtomicInteger();
    private final AtomicInteger executeHitCount = new AtomicInteger();

    @Override
    public void execute(final WorkerContext context) {
        context.onStopping(contextStopEventHitCount::incrementAndGet);
        if (thawOnStop) {
            context.onStopping(() -> isFrozen = false);
        }
        executeHitCount.incrementAndGet();
        lastContext = context;
        hitCount ++;
        while (isFrozen) {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        context.setHasMoreWork(false);
    }

    @Override
    public void close() throws Exception {

    }
}
