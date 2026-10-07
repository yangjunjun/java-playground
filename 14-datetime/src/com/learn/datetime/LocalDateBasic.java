package com.learn.datetime;

import java.time.LocalDate;

/**
 * LocalDate 基础：创建、取值、增减与替换。
 * LocalDate 只有日期没有时间，不可变，线程安全（Java 8+）。
 */
public class LocalDateBasic {

    public static void demo() {
        // now() 取系统默认时区的今天；of(...) 精确构造
        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.of(2000, 5, 20);
        System.out.println("今天: " + today);
        System.out.println("生日: " + birthday);

        // 取值
        System.out.println("年=" + today.getYear()
                + ", 月=" + today.getMonthValue()
                + ", 日=" + today.getDayOfMonth()
                + ", 星期=" + today.getDayOfWeek()
                + ", 今年第 " + today.getDayOfYear() + " 天");

        // plus / minus：返回新对象，原对象不变
        LocalDate nextWeek = today.plusWeeks(1);
        LocalDate lastYear = today.minusYears(1);
        System.out.println("下周今天: " + nextWeek);
        System.out.println("去年今天: " + lastYear);

        // with：替换某个字段
        LocalDate firstOfMonth = today.withDayOfMonth(1);
        System.out.println("本月第一天: " + firstOfMonth);

        // 比较与判断
        System.out.println("生日在今天之前？ " + birthday.isBefore(today));
        System.out.println("是否闰年(2024)？ " + LocalDate.of(2024, 1, 1).isLeapYear());
    }
}
