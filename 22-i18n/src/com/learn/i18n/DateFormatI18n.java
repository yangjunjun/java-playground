package com.learn.i18n;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * 日期时间本地化：DateTimeFormatter.ofLocalized* 与时区。
 */
public class DateFormatI18n {

    public static void demo() {
        LocalDate date = LocalDate.of(2025, 1, 15);
        LocalTime time = LocalTime.of(14, 30, 5);
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        // ---- ofLocalizedDate / Time / DateTime（FormatStyle: FULL/LONG/MEDIUM/SHORT）----
        System.out.println("本地化日期（FULL）:");
        for (Locale loc : new Locale[]{ Locale.CHINA, Locale.US, Locale.JAPAN }) {
            DateTimeFormatter f = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(loc);
            System.out.println("  " + loc + " → " + date.format(f));
        }

        System.out.println("本地化日期时间（MEDIUM）:");
        for (Locale loc : new Locale[]{ Locale.CHINA, Locale.US }) {
            DateTimeFormatter f = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(loc);
            System.out.println("  " + loc + " → " + dateTime.format(f));
        }

        // ---- 自定义模式 + Locale（月份/星期名会本地化）----
        DateTimeFormatter zhPattern = DateTimeFormatter.ofPattern("yyyy年MM月dd日 EEEE", Locale.CHINA);
        DateTimeFormatter usPattern = DateTimeFormatter.ofPattern("MMM dd, yyyy (EEE)", Locale.US);
        System.out.println("自定义中文模式: " + date.format(zhPattern));
        System.out.println("自定义英文模式: " + date.format(usPattern));

        // ---- 时区 ----
        ZonedDateTime shanghai = dateTime.atZone(ZoneId.of("Asia/Shanghai"));
        ZonedDateTime utc = shanghai.withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime ny = shanghai.withZoneSameInstant(ZoneId.of("America/New_York"));
        DateTimeFormatter iso = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        System.out.println("同一时刻不同时区:");
        System.out.println("  上海: " + shanghai.format(iso));
        System.out.println("  UTC:  " + utc.format(iso));
        System.out.println("  纽约: " + ny.format(iso));

        // ---- 解析 ----
        LocalDate parsed = LocalDate.parse("2025-01-15", DateTimeFormatter.ISO_LOCAL_DATE);
        System.out.println("ISO 解析: " + parsed);
        System.out.println("建议：存储/传输用 ISO-8601，展示时再按用户 Locale 格式化");
    }
}
