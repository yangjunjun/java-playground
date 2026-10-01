package com.learn.datetime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * DateTimeFormatter：格式化（format）与解析（parse），ISO 与本地化格式。
 * DateTimeFormatter 不可变且线程安全，可定义为常量复用。
 */
public class DateTimeFormat {

    public static void demo() {
        // 自定义模式：format 输出，parse 解析
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String text = LocalDateTime.now().format(fmt);
        System.out.println("格式化输出: " + text);

        LocalDateTime parsed = LocalDateTime.parse("2024-01-01 08:30:00", fmt);
        System.out.println("解析结果: " + parsed);

        // 只有日期的模式要用 LocalDate 承接
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate date = LocalDate.parse("2024/06/15", dateFmt);
        System.out.println("日期解析: " + date + ", 再输出: " + date.format(dateFmt));

        // ISO 内置格式（推荐跨系统交换时使用）
        System.out.println("ISO 日期: " + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        System.out.println("ISO 时间: " + LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
        System.out.println("ISO 时刻: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // 本地化格式（随 Locale 变化）
        DateTimeFormatter cn = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
                .withLocale(Locale.CHINA);
        System.out.println("中文完整日期: " + LocalDate.now().format(cn));

        // 解析失败抛 DateTimeParseException
        try {
            LocalDate.parse("2024-13-40", dateFmt);
        } catch (DateTimeParseException e) {
            System.out.println("解析失败符合预期: " + e.getMessage());
        }
    }
}
