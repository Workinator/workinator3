package com.allardworks.workinator3.contracts;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class WorkerInfo {
    private final String assignee;
    private final LocalDateTime createDate;
    private final String rule;
}
