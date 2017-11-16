package com.allardworks.workinator3.contracts;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ConsumerConfiguration {
    @Builder.Default
    private final String consumerName = "yadda";
    private final String partitionType;
}
