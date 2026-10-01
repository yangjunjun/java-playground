package com.learn.vthreads;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 并发陷阱与最佳实践（虚拟线程时代）。
 * 覆盖：不要池化虚拟线程、ThreadLocal 泄漏、共享可变状态、CPU 密集型误用。
 */
public class ConcurrencyPitfalls {

    // 陷阱 1：共享可变状态未同步
    private static int unsafeCounter = 0;

    // 正确：原子类或锁
    private static final AtomicInteger safeCounter = new AtomicInteger();

    // 陷阱 2：ThreadLocal 被虚拟线程「复用」时泄漏（虚拟线程不复用，但线程池会）
    private static final ThreadLocal<String> LEAKY = new ThreadLocal<>();

    public static void demo() throws Exception {
        // ---- 陷阱 1：非原子自增 ----
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1000; i++) {
                exec.submit(() -> {
                    unsafeCounter++;               // 读-改-写不是原子
                    safeCounter.incrementAndGet(); // 原子安全
                });
            }
        }
        // 等一下让任务跑完
        Thread.sleep(200);
        System.out.println("非原子自增结果: " + unsafeCounter + "（期望 1000，可能丢失更新）");
        System.out.println("原子自增结果: " + safeCounter.get() + "（稳定 1000）");

        // ---- 陷阱 2：并发集合更省心 ----
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 100; i++) {
                int n = i;
                exec.submit(() -> map.merge("key" + (n % 5), 1, Integer::sum));
            }
        }
        Thread.sleep(100);
        System.out.println("ConcurrentHashMap 合并结果: " + map);

        // ---- 陷阱 3：ThreadLocal 泄漏 ----
        // 虚拟线程不复用，但若线程池用 ThreadLocal 且不 remove，会一直累积
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 3; i++) {
                exec.submit(() -> {
                    LEAKY.set("敏感数据-" + Thread.currentThread().threadId());
                    // 忘记 LEAKY.remove() → 在线程池场景下会泄漏
                });
            }
        }
        Thread.sleep(50);
        System.out.println("ThreadLocal 陷阱：用完必须 remove，或改用 ScopedValue");
        LEAKY.remove();

        // ---- 陷阱 4：CPU 密集型不要用虚拟线程 ----
        // 虚拟线程解决的是「阻塞等待」，不是「计算」
        long start = System.nanoTime();
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 20; i++) {
                exec.submit(() -> {
                    long sum = 0;
                    for (long j = 0; j < 2_000_000; j++) {
                        sum += j;
                    }
                    return sum;
                });
            }
        }
        long ms = (System.nanoTime() - start) / 1_000_000;
        System.out.println("CPU 密集用虚拟线程: " + ms + "ms（不比固定池快，甚至更慢）");

        // ---- 陷阱 5：不要在虚拟线程里 sleep 来「等」共享状态 ----
        // 用 CountDownLatch / CompletableFuture / 结构化并发，而不是忙等
        System.out.println("正确等待：CountDownLatch / CompletableFuture / StructuredTaskScope，避免忙等 sleep");

        // ---- 最佳实践清单 ----
        System.out.println("最佳实践:");
        System.out.println("  1. 虚拟线程随建随用，绝不池化");
        System.out.println("  2. CPU 密集用固定大小平台线程池（= 核数）");
        System.out.println("  3. 共享状态用原子类 / 并发集合 / 锁");
        System.out.println("  4. 用 ScopedValue 替代 ThreadLocal");
        System.out.println("  5. 用 StructuredTaskScope 管理子任务生命周期");

        System.out.println("小结：虚拟线程只优化阻塞吞吐，不改变并发正确性要求");
    }
}
