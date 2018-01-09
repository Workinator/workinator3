package com.allardworks.workinator3.contracts;

import com.allardworks.workinator3.core.NullableOptional;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PartitionDao {
    private String partitionKey;
    private final NullableOptional<LocalDateTime> lastWork = new NullableOptional<>();
    private final NullableOptional<LocalDateTime> lastCheckStart = new NullableOptional<>();;
    private final NullableOptional<LocalDateTime> lastCheckEnd = new NullableOptional<>();;
    private final NullableOptional<Boolean> hasMoreWork = new NullableOptional<>();;
    private final NullableOptional<Long> workCount = new NullableOptional<>();;
    private final NullableOptional<Integer> maxIdleTimeSeconds = new NullableOptional<>();;
    private final NullableOptional<Integer> maxWorkerCount = new NullableOptional<>();
}
