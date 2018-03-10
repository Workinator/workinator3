package com.allardworks.workinator3.core;

import lombok.val;

import java.time.Instant;
import java.util.Date;

public class ConvertUtility {
    private ConvertUtility() {
    }

    public final static Date MIN_DATE = Date.from(Instant.now());
}
