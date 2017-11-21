package com.allardworks.workinator3;

import com.allardworks.workinator3.contracts.WorkerAsync;
import com.allardworks.workinator3.testsupport.DummyWorkerAsync;
import lombok.val;
import org.junit.Test;
import org.springframework.util.Assert;

public class Junk {

    @Test
    public void blah() {
        val worker = DummyWorkerAsync.class;
        val workerType = WorkerAsync.class;

        Assert.isTrue(workerType.isAssignableFrom(worker), "sfasdf");


    }
}
