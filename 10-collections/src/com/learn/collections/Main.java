package com.learn.collections;

/**
 * 集合框架学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. 集合体系结构");
        CollectionHierarchy.demo();

        banner("2. List 与 fail-fast");
        ListDemo.demo();

        banner("3. Set 与 equals/hashCode 契约");
        SetDemo.demo();

        banner("4. Map 家族");
        MapDemo.demo();

        banner("5. Queue / Deque");
        QueueDeque.demo();

        banner("6. Collections 工具类");
        CollectionsUtil.demo();

        banner("7. SequencedCollections（Java 21+）");
        SequencedCollections.demo();

        banner("8. 并发集合（简介）");
        ConcurrentCollectionsBrief.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
