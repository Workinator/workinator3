package com.allardworks.workinator3;

import com.allardworks.workinator3.contracts.Workinator;

public interface WorkinatorTester extends AutoCloseable {
    Workinator getWorkinator();
}
