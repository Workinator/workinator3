package com.allardworks.workinator3.core;

import lombok.val;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class ConvertUtility {
    private ConvertUtility() {
    }

    public static Date toDate(final LocalDateTime from) {
        val zdt = from.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    public static LocalDateTime toLocalDateTime(final Date from) {
        return LocalDateTime.ofInstant(from.toInstant(), ZoneId.systemDefault());
    }
}
