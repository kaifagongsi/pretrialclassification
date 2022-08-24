package com.kfgs.pretrialclassification.common.utils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeUtils {

    public static String getCurrentTime(){

        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    public static String getTime(long timeMillis){
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(timeMillis/1000,0, ZoneOffset.ofHours(8));
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
