package com.learn.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * volatile 可见性、原子类，以及 count++ 竞态条件演示。
 */
public class ConcurrentUtils {

    static class UnsafeCounter {
        private int count;   // 无保护，count++ 非原子

        void increment() {
            count++;
        }

        int get() {
            return count;
        }
    }

    static class AtomicCounter {
        private final AtomicInteger count = new AtomicInteger();

        void increment() {
            count.incrementAndGet();
        }

        int get() {
            return count.get();
        }
    }

    /** volatile 保证可见性：一个线程写，其他线程立刻能看到 */
    static volatile boolean running = true;

    public static void demo() throws InterruptedException {
        // 1) 竞态条件：多线程 count++ 丢失更新
        UnsafeCounter unsafe = new UnsafeCounter();
        AtomicCounter atomic = new AtomicCounter();
        LongAdder adder = new LongAdder();
        AtomicLong atomicLong = new AtomicLong();

        ExecutorService pool = Executors.newFixedThreadPool(4);
        try {
            for (int i = 0; i < 4; i++) {
                pool.submit(() -> {
                    for (int j = 0; j < 1000; j++) {
                        unsafe.increment();
                        atomic.increment();
                        adder.increment();
                        atomicLong.incrementAndGet();
                    }
                });
            }
        } finally {
            pool.shutdown();
            pool.awaitTermination(5, TimeUnit.SECONDS);
        }

        System.out.println("不安全 count++: " + unsafe.get() + "（期望 4000，通常更少——竞态丢失）");
        System.out.println("AtomicInteger:  " + atomic.get() + "（期望 4000）");
        System.out.println("AtomicLong:     " + atomicLong.get() + "（期望 4000）");
        System.out.println("LongAdder:      " + adder.sum() + "（期望 4000，高并发计数推荐）");

        // 2) volatile 可见性：写线程改 running，读线程立刻可见
        Thread reader = new Thread(() -> {
            while (running) {
                // 自旋等待
            }
            System.out.println("volatile: 读线程看到 running=false，退出自旋");
        });
        reader.start();
        Thread.sleep(30);
        running = false;
        reader.join();
    }
}
