package com.learn.stream;

/**
 * Stream API 学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. Stream 基础与惰性求值");
        StreamBasics.demo();

        banner("2. Stream 的创建");
        StreamCreation.demo();

        banner("3. 中间操作");
        IntermediateOps.demo();

        banner("4. 终止操作");
        TerminalOps.demo();

        banner("5. Collectors 收集器");
        CollectorsDemo.demo();

        banner("6. 并行流");
        ParallelStreams.demo();

        banner("7. Stream.toList() 与 Java 16+");
        StreamJava16Plus.demo();

        banner("8. 原始类型流");
        PrimitiveStreams.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
