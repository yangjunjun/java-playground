package com.learn.str;

/**
 * 字符串与文本学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. String 基础");
        StringBasics.demo();

        banner("2. 不可变性");
        StringImmutability.demo();

        banner("3. StringBuilder 与 StringBuffer");
        StringBuilderDemo.demo();

        banner("4. 文本块（Java 15+）");
        TextBlocks.demo();

        banner("5. 格式化与 join");
        StringFormat.demo();

        banner("6. Java 11+ 实用 API");
        StringJava11Plus.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
