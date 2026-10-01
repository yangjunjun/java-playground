package com.learn.regex;

import java.util.regex.Pattern;

/**
 * 性能与回溯：预编译 Pattern、灾难性回溯概念、独占量词防御性写法。
 */
public class RegexPerformance {

    public static void demo() {
        // ---- 建议 1：预编译 Pattern ----
        // String.matches / split / replaceAll 每次调用都会重新编译正则
        Pattern email = Pattern.compile("[\\w.+-]+@[\\w.-]+\\.[a-z]{2,}");
        long t0 = System.nanoTime();
        for (int i = 0; i < 20_000; i++) {
            email.matcher("user" + i + "@example.com").matches();
        }
        long reused = System.nanoTime() - t0;

        t0 = System.nanoTime();
        for (int i = 0; i < 20_000; i++) {
            ("user" + i + "@example.com").matches("[\\w.+-]+@[\\w.-]+\\.[a-z]{2,}");
        }
        long recompiled = System.nanoTime() - t0;
        System.out.println("预编译 2 万次匹配耗时约 " + reused / 1_000_000 + " ms");
        System.out.println("每次重编译耗时约 " + recompiled / 1_000_000 + " ms");
        System.out.println("结论：高频场景请用 Pattern.compile 缓存 Pattern 对象");

        // ---- 建议 2：警惕灾难性回溯（catastrophic backtracking）----
        // (a+)+ 这类“嵌套量词 + 可重叠”结构，理论上失败时要尝试 a 的所有拆分方式，
        // 复杂度约 O(2^n)。经典攻击输入：一长串 a 且故意缺少结尾的 b。
        String evil = "a".repeat(28);
        Pattern bad = Pattern.compile("(a+)+b");
        long start = System.nanoTime();
        boolean hit = bad.matcher(evil).matches();
        long cost = System.nanoTime() - start;
        System.out.println("(a+)+b 对 28 个 a（无结尾 b）匹配结果: " + hit
                + "，耗时约 " + cost / 1_000_000 + " ms");
        System.out.println("注意：新版 OpenJDK 正则引擎已缓解经典回溯爆炸，这里未必卡顿；");
        System.out.println("但同一表达式在 JS / Python / 旧版 Java 或更复杂模式上仍可能指数级卡死。");
        System.out.println("教训：避免嵌套量词，限制输入长度，必要时用独占量词改写。");

        // 独占量词 (a++)+ 不回溯，失败立即返回，任何引擎上都安全
        Pattern possessive = Pattern.compile("(a++)+b");
        start = System.nanoTime();
        hit = possessive.matcher(evil).matches();
        cost = System.nanoTime() - start;
        System.out.println("独占写法 (a++)+b 匹配结果: " + hit + "，耗时约 " + cost / 1_000_000 + " ms");
    }
}
