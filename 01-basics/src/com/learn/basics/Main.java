package com.learn.basics;

/**
 * 语言基础学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. 变量与基本类型");
        VariablesAndTypes.demo();

        banner("2. 运算符");
        Operators.demo();

        banner("3. 控制流程与循环");
        ControlFlow.demo();

        banner("4. 输入与输出");
        InputOutput.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
