package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.Service;
import com.allardworks.workinator3.core.ServiceBase;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created by jaya on 2/26/18.
 * k?
 */
@RequiredArgsConstructor
public class MaintenanceThread extends ServiceBase {
    private final CountDownLatch block = new CountDownLatch(1);
    private Thread thread;

    @Override
    public Map<String, Object> getInfo() {
        return null;
    }

    @Override
    public void start() {
        getServiceStatus().initialize(s -> {
            s.getEventHandlers().onPostStarting(t -> {
                thread = new Thread(this::run);
                thread.start();
            });

            // the RUN loop blocks on the countdown latch.
            // release the block when stopping.
            s.getEventHandlers().onPostStopping(t -> {
                block.countDown();
            });
        });
        super.start();
    }

    protected Duration getDelay() {
        return Duration.ofMillis(25000);
    }

    protected void updateWorkerStatus() {
        //System.out.println("\n\n" + LocalDateTime.now() + " I'm working here.");
    }

    private void run() {
        getServiceStatus().started();
        while (getServiceStatus().getStatus().isStarted()) {
            updateWorkerStatus();

            try {
                // basically a sleep. it releases
                // every 10 seconds, or when the block is released.
                // the block is released when the service stops.
                block.await(getDelay().toMillis(), MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        getServiceStatus().stopped();
    }
}
