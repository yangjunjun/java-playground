package com.learn.modern;

/**
 * 现代语法特性学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. var 局部变量类型推断（Java 10+）");
        VarAndTypeInference.demo();

        banner("2. switch 表达式（Java 14+）");
        SwitchExpressions.demo();

        banner("3. 文本块 Text Block（Java 15+）");
        TextBlocks.demo();

        banner("4. record 记录类（Java 16+）");
        Records.demo();

        banner("5. sealed 密封类（Java 17+）");
        SealedClasses.demo();

        banner("6. 模式匹配（instanceof Java 16+ / switch Java 21+）");
        PatternMatching.demo();

        banner("7. 匿名模式与匿名变量（Java 21+/22+）");
        UnnamedPatterns.demo();

        banner("8. 构造器中的灵活语句（Java 22+）");
        FlexibleCtorBrief.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
