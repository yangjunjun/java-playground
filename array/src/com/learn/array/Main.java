package com.learn.array;

/**
 * 数组学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. 数组基础");
        ArrayBasics.demo();

        banner("2. 数组复制");
        ArrayCopy.demo();

        banner("3. 多维数组");
        MultiDimArray.demo();

        banner("4. java.util.Arrays 工具类");
        ArraysUtil.demo();

        banner("5. 排序与查找");
        ArraySortSearch.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
