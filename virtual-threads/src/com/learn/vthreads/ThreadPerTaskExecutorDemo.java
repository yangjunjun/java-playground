package com.learn.vthreads;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

/**
 * 每任务一线程执行器（Java 21+）。
 * newVirtualThreadPerTaskExecutor：每个任务一个虚拟线程，不要再池化虚拟线程。
 */
public class ThreadPerTaskExecutorDemo {

    public static void demo() throws Exception {
        // ---- 旧思维：池化平台线程（有上限，任务排队）----
        long start1 = System.nanoTime();
        try (var pool = Executors.newFixedThreadPool(8)) { // 只有 8 个工人
            var futures = IntStream.range(0, 32)
                    .mapToObj(i -> pool.submit(() -> {
                        sleep(20);
                        return "任务" + i;
                    }))
                    .toList();
            for (Future<String> f : futures) {
                f.get();
            }
        }
        long ms1 = (System.nanoTime() - start1) / 1_000_000;
        System.out.println("固定 8 线程池跑 32 个各 20ms 任务: " + ms1 + "ms（受池大小限制，任务排队）");

        // ---- 新思维：每任务一个虚拟线程，不池化 ----
        long start2 = System.nanoTime();
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = IntStream.range(0, 32)
                    .mapToObj(i -> exec.submit(() -> {
                        sleep(20);
                        return "任务" + i;
                    }))
                    .toList();
            for (Future<String> f : futures) {
                f.get();
            }
        }
        long ms2 = (System.nanoTime() - start2) / 1_000_000;
        System.out.println("每任务一虚拟线程跑 32 个各 20ms 任务: " + ms2 + "ms（几乎全并发）");

        // ---- 关键提醒：不要池化虚拟线程 ----
        // 虚拟线程创建/销毁几乎无成本，池化反而引入排队与上下文错乱
        // 正确做法：随用随建，任务结束即销毁
        System.out.println("提醒：虚拟线程不要池化，直接 newVirtualThreadPerTaskExecutor 或 Thread.ofVirtual()");

        // ---- CPU 密集型仍应使用固定大小平台线程池 ----
        int cpuCores = Runtime.getRuntime().availableProcessors();
        try (var cpuPool = Executors.newFixedThreadPool(cpuCores)) {
            System.out.println("CPU 密集型任务建议固定 " + cpuCores + " 线程（= 核数），虚拟线程帮不上忙");
        }

        System.out.println("小结：IO 密集用虚拟线程随建随用，CPU 密集用固定平台线程池");
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
