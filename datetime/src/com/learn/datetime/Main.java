package com.learn.datetime;

/**
 * 日期时间学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. LocalDate / LocalTime / LocalDateTime");
        LocalDateBasic.demo();

        banner("2. LocalTime 与 LocalDateTime 深入");
        LocalTimeAndDateTime.demo();

        banner("3. ZonedDateTime 与 Duration / Period");
        ZonedAndDuration.demo();

        banner("4. Instant 与 Clock");
        InstantAndClock.demo();

        banner("5. DateTimeFormatter 格式化与解析");
        DateTimeFormat.demo();

        banner("6. 旧 Date / Calendar 与新 API 互转");
        DateLegacy.demo();

        banner("7. 常用日期工具方法");
        DateTimeUtils.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
