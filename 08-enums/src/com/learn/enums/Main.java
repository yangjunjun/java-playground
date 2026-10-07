package com.learn.enums;

/**
 * 枚举学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. 枚举基础");
        EnumBasics.demo();

        banner("2. 枚举字段、构造器与方法");
        EnumWithFields.demo();

        banner("3. 枚举常用方法与比较");
        EnumMethods.demo();

        banner("4. 抽象方法与接口实现");
        EnumAbstractAndInterface.demo();

        banner("5. EnumSet / EnumMap 与 switch");
        EnumPatterns.demo();

        banner("6. 枚举陷阱与选型建议");
        EnumPitfalls.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
