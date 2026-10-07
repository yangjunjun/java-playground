package com.learn.exception;

/**
 * 异常处理学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. try-catch-finally 执行顺序");
        TryCatchFinally.demo();

        banner("2. 受检异常 vs 非受检异常");
        CheckedVsUnchecked.demo();

        banner("3. 自定义异常");
        CustomException.demo();

        banner("4. try-with-resources 与被抑制异常");
        TryWithResources.demo();

        banner("5. 多重捕获、重抛与堆栈");
        MultiCatchAndFinally.demo();

        banner("6. 异常处理最佳实践");
        ExceptionBestPractices.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
