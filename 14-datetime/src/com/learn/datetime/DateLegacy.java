package com.learn.datetime;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 旧 java.util.Date / Calendar 的问题，以及与 java.time 的互转。
 */
public class DateLegacy {

    @SuppressWarnings("deprecation")
    public static void demo() {
        // 旧 API：Date 可变、月份从 0 开始、时区语义混乱
        Date date = new Date();
        System.out.println("旧 Date: " + date);
        System.out.println("旧 getMonth() 返回 0 表示一月: " + date.getMonth());

        // Calendar 同样可变，且是遗留设计
        Calendar cal = new GregorianCalendar();
        cal.set(2024, Calendar.MARCH, 15); // 月份常量 2 = 三月
        System.out.println("Calendar 月份常量 MARCH = " + Calendar.MARCH + "（即三月）");

        // ---- 互转 ----
        // Date -> Instant -> java.time
        Instant instant = date.toInstant();
        LocalDate fromDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        System.out.println("Date 转 LocalDate: " + fromDate);

        // java.time -> Date
        LocalDate ld = LocalDate.now();
        Date back = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
        System.out.println("LocalDate 转 Date: " + back);

        // Calendar -> ZonedDateTime（GregorianCalendar 直接支持）
        GregorianCalendar gc = new GregorianCalendar();
        System.out.println("Calendar 转 ZonedDateTime: " + gc.toZonedDateTime());
    }
}
