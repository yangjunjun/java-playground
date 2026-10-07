package com.learn.datetime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * 常用日期工具：between 求差、首末日、下个工作日等 TemporalAdjusters 用法。
 */
public class DateTimeUtils {

    public static void demo() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 12, 31);

        // ChronoUnit.between：精确差值
        long days = ChronoUnit.DAYS.between(start, end);
        long weeks = ChronoUnit.WEEKS.between(start, end);
        System.out.println("相差 " + days + " 天（约 " + weeks + " 周）");

        LocalDateTime t1 = LocalDateTime.of(2024, 1, 1, 8, 0);
        LocalDateTime t2 = LocalDateTime.of(2024, 1, 1, 10, 30);
        System.out.println("相差 " + ChronoUnit.MINUTES.between(t1, t2) + " 分钟");

        // 本月第一天 / 最后一天
        LocalDate today = LocalDate.now();
        LocalDate first = today.withDayOfMonth(1);
        LocalDate last = today.with(TemporalAdjusters.lastDayOfMonth());
        System.out.println("本月第一天: " + first + ", 最后一天: " + last);

        // 下一个周一、下个月同一天
        System.out.println("下周一: " + today.with(TemporalAdjusters.next(java.time.DayOfWeek.MONDAY)));
        System.out.println("下月同日: " + today.plusMonths(1));

        // 闰年判断
        System.out.println("2024 闰年？ " + Year.of(2024).isLeap());
        System.out.println("2023 闰年？ " + Year.of(2023).isLeap());
    }
}
