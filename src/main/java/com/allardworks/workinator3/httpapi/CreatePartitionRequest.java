package com.allardworks.workinator3.httpapi;

import lombok.Data;

/**
 * Created by jaya on 3/5/18.
 * k?
 */
@Data
public class CreatePartitionRequest {
    private int maxWorkerCount = 1;
    private int maxIdleTimeSeconds = 10;
}
