package com.allardworks.workinator3.contracts;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@EqualsAndHashCode
@Getter
@Component
public class ConsumerId {
    public ConsumerId(@Value("${consumerid.name}") String name) {
        this.name=name;
    }

    private final String name;
}
