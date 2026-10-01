package com.learn.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * synchronized（实例/类/同步块）与 ReentrantLock 显式锁。
 */
public class SyncAndLock {

    /** 演示 synchronized 保护的计数器 */
    static class SafeCounter {
        private int count;

        synchronized void increment() {   // 锁当前实例 this
            count++;
        }

        synchronized int get() {
            return count;
        }
    }

    /** 演示 ReentrantLock 保护的计数器 */
    static class LockCounter {
        private final ReentrantLock lock = new ReentrantLock();
        private int count;

        void increment() {
            lock.lock();
            try {
                count++;
            } finally {
                lock.unlock();   // 必须 finally 释放
            }
        }

        int get() {
            lock.lock();
            try {
                return count;
            } finally {
                lock.unlock();
            }
        }
    }

    public static void demo() throws InterruptedException {
        SafeCounter safe = new SafeCounter();
        LockCounter locked = new LockCounter();

        ExecutorService pool = Executors.newFixedThreadPool(4);
        try {
            for (int i = 0; i < 4; i++) {
                pool.submit(() -> {
                    for (int j = 0; j < 1000; j++) {
                        safe.increment();
                        locked.increment();
                    }
                });
            }
        } finally {
            pool.shutdown();
            pool.awaitTermination(5, TimeUnit.SECONDS);
        }

        System.out.println("synchronized 计数: " + safe.get() + "（期望 4000）");
        System.out.println("ReentrantLock 计数: " + locked.get() + "（期望 4000）");

        // synchronized 三种写法示意
        synchronized (SyncAndLock.class) {          // 锁 Class 对象
            System.out.println("synchronized(类) 临界区执行");
        }
    }
}
