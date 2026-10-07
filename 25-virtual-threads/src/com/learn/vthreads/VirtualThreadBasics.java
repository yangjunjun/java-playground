package com.learn.vthreads;

/**
 * 虚拟线程基础（Java 21+ 正式）。
 * 虚拟线程是 JVM 调度的轻量线程：堆上栈帧、创建成本极低，适合大量并发阻塞任务。
 */
public class VirtualThreadBasics {

    public static void demo() throws Exception {
        // ---- 创建方式 1：Thread.ofVirtual() ----
        Thread t1 = Thread.ofVirtual().name("vt-hello").start(() -> {
            System.out.println("  [ofVirtual] 线程名=" + Thread.currentThread().getName()
                    + ", virtual=" + Thread.currentThread().isVirtual());
        });
        t1.join();

        // ---- 创建方式 2：Thread.startVirtualThread（快捷方法）----
        Thread t2 = Thread.startVirtualThread(() -> {
            System.out.println("  [startVirtualThread] virtual=" + Thread.currentThread().isVirtual());
        });
        t2.join();

        // ---- 创建方式 3：Executors.newVirtualThreadPerTaskExecutor ----
        try (var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(() -> {
                System.out.println("  [Executor] virtual=" + Thread.currentThread().isVirtual());
            }).get();
        }

        // ---- 传统平台线程作对比 ----
        Thread platform = new Thread(() -> {
            System.out.println("  [平台线程] virtual=" + Thread.currentThread().isVirtual());
        });
        platform.start();
        platform.join();

        // ---- 为什么 100 万个虚拟线程也便宜 ----
        // 平台线程：每个线程约 1MB 栈（OS 级），100 万个要 ~1TB 内存，不可行
        // 虚拟线程：栈在堆上按需增长，100 万个只需几十 MB
        int count = 100_000; // 示例用 10 万，真正压力测试可用 100 万
        long start = System.nanoTime();
        var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor();
        var futures = new java.util.ArrayList<java.util.concurrent.Future<?>>();
        for (int i = 0; i < count; i++) {
            futures.add(executor.submit(() -> {
                try {
                    Thread.sleep(10); // 阻塞也便宜：虚拟线程会卸载载体线程
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));
        }
        for (var f : futures) {
            f.get();
        }
        executor.close();
        long ms = (System.nanoTime() - start) / 1_000_000;
        System.out.println("  启动并等待 " + count + " 个虚拟线程（各 sleep 10ms）耗时: " + ms + "ms");

        System.out.println("小结：虚拟线程创建极便宜，阻塞也便宜，适合 IO 密集型海量并发");
    }
}
