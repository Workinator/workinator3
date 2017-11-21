package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.contracts.Service;
import lombok.val;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

public class TestUtility {
    private TestUtility() {
    }

    public static void startAndWait(final Service service) {
        val countdown = new CountDownLatch(1);
        service.getTransitionEventHandlers().onPostStarted(t -> countdown.countDown());
        service.start();
        try {
            countdown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void stopAndWait(final Service service) {
        val countdown = new CountDownLatch(1);
        service.getTransitionEventHandlers().onPostStopped(t -> countdown.countDown());
        service.stop();
        try {
            countdown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void waitFor(final Supplier<Boolean> done) {
        val start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 1000) {
            if (done.get()) {
                return;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Assert.fail("waitFor didn't finish in time.");
    }
}
