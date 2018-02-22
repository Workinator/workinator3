package com.allardworks.workinator3.core;

import lombok.val;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class ConvertUtility {
    private ConvertUtility() {
    }

    public static LocalDateTime MIN_DATE = LocalDateTime.of(2000, 1, 1, 0, 0);

    public static Date toDate(final LocalDateTime from) {
        if (from == null) {
            return null;
        }
        val zdt = from.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    public static LocalDateTime toLocalDateTime(final Date from) {
        if (from == null) {
            return null;
        }
        return LocalDateTime.ofInstant(from.toInstant(), ZoneId.systemDefault());
    }
}
