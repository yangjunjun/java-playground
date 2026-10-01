package com.learn.datetime;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Instant：时间线上的绝对时刻（UTC 时间戳）；Clock：可注入的时钟抽象。
 */
public class InstantAndClock {

    public static void demo() {
        // Instant.now()：UTC 时间戳，机器友好
        Instant now = Instant.now();
        System.out.println("当前时刻: " + now);
        System.out.println("epoch 秒: " + now.getEpochSecond() + ", 毫秒: " + now.toEpochMilli());

        // 从字符串解析
        Instant parsed = Instant.parse("2024-01-01T00:00:00Z");
        System.out.println("解析: " + parsed);

        // Instant 加减
        System.out.println("30 天后: " + now.plus(Duration.ofDays(30)));

        // Clock：把“当前时间”抽象出来，便于测试注入固定时钟
        Clock systemClock = Clock.systemUTC();
        Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
        Clock offsetClock = Clock.offset(systemClock, Duration.ofHours(8));

        System.out.println("系统时钟: " + systemClock.instant());
        System.out.println("固定时钟: " + fixedClock.instant());   // 永远是 2024-01-01T00:00:00Z
        System.out.println("偏移时钟: " + offsetClock.instant());

        // Instant 转带时区的时间
        System.out.println("北京时间: " + now.atZone(ZoneId.of("Asia/Shanghai")));
    }
}
