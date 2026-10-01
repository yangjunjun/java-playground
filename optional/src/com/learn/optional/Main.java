package com.learn.optional;

/**
 * Optional 学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. 为什么需要 Optional");
        OptionalBasics.demo();

        banner("2. Optional 的创建");
        OptionalCreation.demo();

        banner("3. map / flatMap / filter");
        OptionalMapFlatMap.demo();

        banner("4. 取值与兜底");
        OptionalOrElse.demo();

        banner("5. 常见使用模式");
        OptionalPatterns.demo();

        banner("6. 反模式与易错点");
        OptionalPitfalls.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
