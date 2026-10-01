package com.learn.wrapper;

/**
 * 包装类与装箱学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. 包装类基础");
        WrapperBasics.demo();

        banner("2. 自动装箱与拆箱");
        Autoboxing.demo();

        banner("3. Integer 缓存陷阱");
        CachePitfall.demo();

        banner("4. 解析与转换");
        ParseUtils.demo();

        banner("5. 比较与选择");
        WrapperCompare.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
