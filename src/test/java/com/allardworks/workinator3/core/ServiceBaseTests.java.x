package com.allardworks.workinator3.core;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class ServiceBaseTests {
    @Test
    public void startStopImmediate() {
        val startEventCount = new AtomicInteger();
        val stopEventCount = new AtomicInteger();
        try (val service = new ServiceBaseTester()) {
            service.autoStart = true;
            service.autoStop = true;

            // assign event handlers. make sure they're hit at appropriate time.
            service.onStarted(s -> startEventCount.incrementAndGet());
            service.onStarted(s -> startEventCount.incrementAndGet());
            service.onStopped(s -> stopEventCount.incrementAndGet());
            service.onStopped(s -> stopEventCount.incrementAndGet());

            Assert.assertEquals(0, startEventCount.get());
            service.start();
            Assert.assertEquals(Status.Started, service.getStatus());
            Assert.assertEquals(2, startEventCount.get());

            Assert.assertEquals(0, stopEventCount.get());
            service.stop();
            Assert.assertEquals(Status.Stopped, service.getStatus());
            Assert.assertEquals(2, stopEventCount.get());
        }
    }

    @Test
    public void startStopDeferred() {
        val startEventCount = new AtomicInteger();
        val stopEventCount = new AtomicInteger();
        try (val service = new ServiceBaseTester()) {
            service.autoStart = false;
            service.autoStop = false;

            // assign event handlers. make sure they're hit at appropriate time.
            service.onStarted(s -> startEventCount.incrementAndGet());
            service.onStarted(s -> startEventCount.incrementAndGet());
            service.onStopped(s -> stopEventCount.incrementAndGet());
            service.onStopped(s -> stopEventCount.incrementAndGet());

            Assert.assertEquals(0, startEventCount.get());
            service.start();
            Assert.assertEquals(0, startEventCount.get());
            Assert.assertEquals(Status.Starting, service.getStatus());
            service.finishStart();
            Assert.assertEquals(2, startEventCount.get());
            Assert.assertEquals(Status.Started, service.getStatus());

            Assert.assertEquals(0, stopEventCount.get());
            service.stop();
            Assert.assertEquals(0, stopEventCount.get());
            Assert.assertEquals(Status.Stopping, service.getStatus());
            service.finishStop();
            Assert.assertEquals(2, stopEventCount.get());
            Assert.assertEquals(Status.Stopped, service.getStatus());
        }
    }


    public class ServiceBaseTester extends ServiceBase {
        private Runnable stop;

        public void finishStart() {
            //start.run();
        }

        public void finishStop() {
            stop.run();
        }

        @Getter
        @Setter
        public boolean autoStart;

        @Getter
        @Setter
        public boolean autoStop;

        @Override
        protected void onPreStarting() {
        }

        @Override
        protected void stoppingService() {
        }
    }
}
