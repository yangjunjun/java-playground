package com.learn.datetime;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;

/**
 * ZonedDateTime / ZoneId / ZoneOffset 与时间差 Duration / Period。
 * ZoneId 带夏令时规则，优于旧的 TimeZone。
 */
public class ZonedAndDuration {

    public static void demo() {
        // ZoneId：带规则的时区（推荐）
        ZoneId shanghai = ZoneId.of("Asia/Shanghai");
        ZoneId newYork = ZoneId.of("America/New_York");
        ZonedDateTime zdt = ZonedDateTime.now(shanghai);
        System.out.println("上海此刻: " + zdt);

        // 同一瞬间，换到另一个时区显示
        ZonedDateTime nyTime = zdt.withZoneSameInstant(newYork);
        System.out.println("对应纽约: " + nyTime);

        // ZoneOffset：固定偏移量（无夏令时）
        ZonedDateTime fixed = ZonedDateTime.of(LocalDateTime.now(), ZoneOffset.ofHours(8));
        System.out.println("固定 +08:00: " + fixed);

        // Duration：基于时间的差（时/分/秒）
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 1, 10, 30);
        Duration duration = Duration.between(start, end);
        System.out.println("耗时小时数: " + duration.toHours()
                + ", 分钟数: " + duration.toMinutes());

        Duration d2 = Duration.ofHours(2).plusMinutes(30);
        System.out.println("2小时+30分 = " + d2.toMinutes() + " 分钟");

        // Period：基于日期的差（年/月/日）
        LocalDate birth = LocalDate.of(2000, 5, 20);
        Period age = Period.between(birth, LocalDate.of(2024, 5, 21));
        System.out.println("年龄: " + age.getYears() + " 岁 " + age.getMonths() + " 个月 " + age.getDays() + " 天");
    }
}
