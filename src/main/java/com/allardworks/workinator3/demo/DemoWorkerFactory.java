package com.allardworks.workinator3.demo;

import com.allardworks.workinator3.contracts.*;
import org.springframework.stereotype.Component;

@Component
public class DemoWorkerFactory implements AsyncWorkerFactory {
    @Override
    public AsyncWorker createWorker(Assignment assignment) {
        return new DemoWorker();
    }
}
