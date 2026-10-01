package com.learn.modules;

/**
 * Java 模块系统 JPMS 学习总入口：依次运行各知识点示例。
 * 最后还会调用 multi/ 下的真正双模块编译运行示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. 模块基础：Module / ModuleDescriptor");
        ModuleBasics.demo();

        banner("2. 封装：公开类型 vs 包导出");
        PackageEncapsulationDemo.demo();

        banner("3. ServiceLoader 服务发现");
        ServiceLoaderDemo.demo();

        banner("4. 模块路径 vs 类路径");
        ModulePathVsClasspath.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
        System.out.println("（真正的双模块编译见 multi/run.sh，本 run.sh 末尾会自动调用）");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
