package com.learn.regex;

/**
 * 正则表达式学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. Pattern / Matcher 基础");
        PatternMatcher.demo();

        banner("2. 正则语法：字符类与量词");
        RegexSyntax.demo();

        banner("3. 分组与替换");
        GroupAndReplace.demo();

        banner("4. 匹配标志");
        RegexFlags.demo();

        banner("5. 性能与回溯");
        RegexPerformance.demo();

        banner("6. Java 11+ 便捷 API");
        RegexJava11Plus.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
