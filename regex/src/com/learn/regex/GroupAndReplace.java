package com.learn.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 分组与替换：捕获组、命名组（Java 7+）、非捕获组、replaceAll / replaceFirst / appendReplacement。
 */
public class GroupAndReplace {

    public static void demo() {
        // ---- 捕获组 ----
        Pattern date = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
        Matcher m = date.matcher("发布日期 2024-07-01 上线");
        if (m.find()) {
            System.out.println("整串 group(0) = " + m.group());
            System.out.println("年 group(1) = " + m.group(1)
                    + ", 月 group(2) = " + m.group(2)
                    + ", 日 group(3) = " + m.group(3));
        }

        // ---- 命名分组（Java 7+）----
        Pattern named = Pattern.compile("(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})");
        Matcher mn = named.matcher("2025-01-15");
        if (mn.matches()) {
            System.out.println("命名组 year = " + mn.group("year")
                    + ", month = " + mn.group("month")
                    + ", day = " + mn.group("day"));
        }

        // ---- 非捕获组 (?:...)：只分组不占编号，省去无用捕获开销 ----
        Pattern nonCap = Pattern.compile("(?:https?)://([\\w.-]+)");
        Matcher mn2 = nonCap.matcher("访问 https://example.com 与 http://foo.org");
        while (mn2.find()) {
            System.out.println("主机名 = " + mn2.group(1));
        }

        // ---- replaceFirst / replaceAll（$1 引用捕获组）----
        String msg = "用户 张三 、 李四 你好";
        System.out.println("replaceAll 去空格: " + msg.replaceAll("\\s+", ""));
        String csv = "2024-07-01,2025-01-15";
        System.out.println("替换日期格式: " + csv.replaceAll("(\\d{4})-(\\d{2})-(\\d{2})", "$3/$2/$1"));

        // 命名组也能用 ${name} 引用
        String renamed = "2025-01-15".replaceAll(
                "(?<y>\\d{4})-(?<m>\\d{2})-(?<d>\\d{2})",
                "${d}/${m}/${y}");
        System.out.println("命名组替换: " + renamed);

        // ---- appendReplacement / appendTail：逐段拼装结果 ----
        Matcher digits = Pattern.compile("\\d+").matcher("a1b22c333");
        StringBuilder sb = new StringBuilder();
        while (digits.find()) {
            digits.appendReplacement(sb, "[" + digits.group() + "]");
        }
        digits.appendTail(sb);
        System.out.println("appendReplacement 结果: " + sb);
    }
}
