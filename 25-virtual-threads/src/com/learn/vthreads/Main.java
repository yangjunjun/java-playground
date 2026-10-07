package com.learn.vthreads;

/**
 * 虚拟线程与并发新特性学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) throws Exception {
        banner("1. 虚拟线程基础（Java 21+）");
        VirtualThreadBasics.demo();

        banner("2. 每任务一线程执行器（Java 21+）");
        ThreadPerTaskExecutorDemo.demo();

        banner("3. 挂载与钉住 Mounting vs Pinning");
        MountingVsPinning.demo();

        banner("4. 结构化并发 StructuredTaskScope（JDK 25 预览 API）");
        StructuredConcurrencyDemo.demo();

        banner("5. 作用域值 ScopedValue（Java 25 正式）");
        ScopedValuesDemo.demo();

        banner("6. 并发陷阱与最佳实践");
        ConcurrencyPitfalls.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
