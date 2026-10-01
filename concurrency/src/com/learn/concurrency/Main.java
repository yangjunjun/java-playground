package com.learn.concurrency;

/**
 * 并发编程学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) throws Exception {
        banner("1. Thread 基础与生命周期");
        ThreadBasics.demo();

        banner("2. Runnable / Callable / Future");
        RunnableCallable.demo();

        banner("3. synchronized 与 ReentrantLock");
        SyncAndLock.demo();

        banner("4. wait / notify 与 Condition");
        WaitNotifyDemo.demo();

        banner("5. volatile、原子类与竞态");
        ConcurrentUtils.demo();

        banner("6. ThreadLocal");
        ThreadLocalDemo.demo();

        banner("7. ExecutorService 线程池");
        ExecutorDemo.demo();

        banner("8. CompletableFuture 异步编排");
        CompletableFutureDemo.demo();

        banner("9. ConcurrentHashMap");
        ConcurrentHashMapDemo.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
