package com.learn.lambda;

/**
 * Lambda 与函数式接口学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. Lambda 语法与目标类型");
        LambdaSyntax.demo();

        banner("2. 函数式接口与 @FunctionalInterface");
        FunctionalInterfaces.demo();

        banner("3. 方法引用四种形式");
        MethodReferences.demo();

        banner("4. 函数式组合");
        FunctionalComposition.demo();

        banner("5. 变量捕获与 this 语义");
        LambdaCapture.demo();

        banner("6. 内置函数式接口与典型应用");
        BuiltInFunctionals.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
