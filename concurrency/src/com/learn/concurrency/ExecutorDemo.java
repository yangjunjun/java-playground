package com.learn.concurrency;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ExecutorService：线程池提交任务、invokeAll、shutdown；
 * ScheduledExecutorService：延迟与周期任务。
 */
public class ExecutorDemo {

    public static void demo() throws InterruptedException {
        // 1) 固定大小线程池
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            for (int i = 1; i <= 4; i++) {
                final int id = i;
                pool.submit(() ->
                        System.out.println("  任务 " + id + " 由 " + Thread.currentThread().getName() + " 执行"));
            }

            // invokeAll：批量提交并等待全部完成
            Callable<String> t1 = () -> "结果A";
            Callable<String> t2 = () -> "结果B";
            try {
                List<java.util.concurrent.Future<String>> futures = pool.invokeAll(List.of(t1, t2));
                for (var f : futures) {
                    System.out.println("  invokeAll 得到: " + f.get());
                }
            } catch (java.util.concurrent.ExecutionException e) {
                System.out.println("  任务执行失败: " + e.getCause());
            }
        } finally {
            pool.shutdown();
            pool.awaitTermination(3, TimeUnit.SECONDS);
        }

        // 2) 缓存线程池（按需创建，空闲回收）
        ExecutorService cache = Executors.newCachedThreadPool();
        try {
            cache.submit(() -> System.out.println("  缓存池任务: " + Thread.currentThread().getName()));
        } finally {
            cache.shutdown();
            cache.awaitTermination(3, TimeUnit.SECONDS);
        }

        // 3) ScheduledExecutorService：延迟 + 周期
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        try {
            scheduler.schedule(() -> System.out.println("  延迟 50ms 后执行"), 50, TimeUnit.MILLISECONDS);

            var periodic = scheduler.scheduleAtFixedRate(
                    () -> System.out.println("  周期任务（只跑一次演示）"),
                    20, 20, TimeUnit.MILLISECONDS);
            Thread.sleep(70);
            periodic.cancel(true);
        } finally {
            scheduler.shutdown();
            scheduler.awaitTermination(3, TimeUnit.SECONDS);
        }

        System.out.println("提示: Java 21+ 可用 Executors.newVirtualThreadPerTaskExecutor()（见虚拟线程模块）");
    }
}
