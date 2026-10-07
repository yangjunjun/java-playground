package com.learn.vthreads;

/**
 * 挂载（Mounting）与钉住（Pinning）。
 * 虚拟线程运行时「挂载」到载体线程（carrier）；阻塞时可「卸载」让载体去跑别的虚拟线程。
 * 但某些操作会让虚拟线程「钉住」载体，无法卸载，影响并发吞吐。
 */
public class MountingVsPinning {

    private static final Object LOCK = new Object();
    private static int counter = 0;

    public static void demo() throws Exception {
        // ---- 正常情况：sleep / IO 阻塞时虚拟线程会卸载载体 ----
        long start1 = System.nanoTime();
        try (var exec = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new java.util.ArrayList<java.util.concurrent.Future<?>>();
            for (int i = 0; i < 100; i++) {
                futures.add(exec.submit(() -> {
                    try {
                        Thread.sleep(50); // 阻塞：卸载载体线程，其他虚拟线程可跑
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }));
            }
            for (var f : futures) {
                f.get();
            }
        }
        long ms1 = (System.nanoTime() - start1) / 1_000_000;
        System.out.println("100 个虚拟线程各 sleep 50ms（可卸载）: " + ms1 + "ms，几乎并发完成");

        // ---- 钉住场景：synchronized 块内阻塞（历史问题，JDK 24+ 已大幅缓解）----
        // 旧版本 JDK 中，synchronized 内阻塞会钉住载体线程；
        // JDK 24+（JEP 491）已允许在 synchronized 中卸载，不再钉住。
        // 但 JNI / native 方法、长时间占用 synchronized 的场景仍需注意。
        System.out.println("注意：JDK 24+ 之前 synchronized 会钉住载体线程，现已缓解");

        // 演示：用 synchronized 保护的计数器（等价于以前会 pin 的写法）
        long start2 = System.nanoTime();
        try (var exec = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new java.util.ArrayList<java.util.concurrent.Future<?>>();
            for (int i = 0; i < 100; i++) {
                futures.add(exec.submit(() -> {
                    synchronized (LOCK) {
                        counter++;
                        try {
                            Thread.sleep(5); // 旧版 JDK 会在此钉住载体；24+ 不再钉
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }));
            }
            for (var f : futures) {
                f.get();
            }
        }
        long ms2 = (System.nanoTime() - start2) / 1_000_000;
        System.out.println("100 个虚拟线程 synchronized + sleep 5ms: " + ms2 + "ms（JDK 24+ 不再钉住）");
        System.out.println("计数器最终值（应为 100）: " + counter);

        // ---- 如何诊断钉住 ----
        // 运行时加 JVM 参数可打印钉住事件：
        //   java -Djdk.tracePinnedThreads=full ...
        System.out.println("诊断钉住: 加 JVM 参数 -Djdk.tracePinnedThreads=full");

        // ---- 仍然钉住的情况 ----
        System.out.println("仍会钉住: JNI/native 方法阻塞、长时间占用 synchronized 的临界区");

        System.out.println("小结：优先用 java.util.concurrent.locks.ReentrantLock，少用 synchronized + 阻塞");
    }
}
