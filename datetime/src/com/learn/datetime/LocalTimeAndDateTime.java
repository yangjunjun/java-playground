package com.learn.datetime;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;

/**
 * LocalTime 与 LocalDateTime：时间/日期时间的创建、增减、截断与比较。
 */
public class LocalTimeAndDateTime {

    public static void demo() {
        // LocalTime：只有时间没有日期
        LocalTime workStart = LocalTime.of(9, 0);
        LocalTime nowTime = LocalTime.now();
        System.out.println("上班时间: " + workStart + ", 当前时间: " + nowTime);
        System.out.println("下午茶: " + LocalTime.of(15, 30, 45, 123_456_789));

        // truncatedTo：截断精度
        LocalTime t = LocalTime.of(14, 30, 45, 123_456_789);
        System.out.println("截断到分钟: " + t.truncatedTo(ChronoUnit.MINUTES));
        System.out.println("截断到小时: " + t.truncatedTo(ChronoUnit.HOURS));

        // LocalDateTime：日期 + 时间
        LocalDateTime meeting = LocalDateTime.of(2024, Month.MARCH, 15, 14, 30);
        System.out.println("会议: " + meeting);

        LocalDateTime later = meeting.plusHours(2).plusMinutes(15);
        System.out.println("两小时一刻钟后: " + later);

        // with 系列：整体替换
        System.out.println("改为当天 18:00: " + meeting.withHour(18).withMinute(0));

        // 日期部分 / 时间部分拆分
        System.out.println("日期部分: " + meeting.toLocalDate() + ", 时间部分: " + meeting.toLocalTime());
    }
}
