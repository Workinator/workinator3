package com.allardworks.workinator3.demo;

import com.allardworks.workinator3.contracts.Assignment;
import com.allardworks.workinator3.contracts.AsyncWorker;
import com.allardworks.workinator3.contracts.AsyncWorkerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("Temporary")
@Component
public class DemoWorkerFactory implements AsyncWorkerFactory {
    @Override
    public AsyncWorker createWorker(Assignment assignment) {
        return new DemoWorker();
    }
}
