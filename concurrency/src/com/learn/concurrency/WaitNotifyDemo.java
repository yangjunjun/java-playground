package com.learn.concurrency;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * wait / notify（Object，须在 synchronized 内）与
 * Condition.await / signal（推荐写法）。
 */
public class WaitNotifyDemo {

    /** 有界队列：wait/notify 实现 */
    static class WaitNotifyQueue {
        private final Queue<Integer> queue = new LinkedList<>();
        private final int capacity = 2;

        synchronized void put(int v) throws InterruptedException {
            while (queue.size() == capacity) {   // while 防虚假唤醒
                wait();
            }
            queue.add(v);
            System.out.println("  [wait/notify] 放入 " + v + ", 当前 " + queue);
            notifyAll();
        }

        synchronized int take() throws InterruptedException {
            while (queue.isEmpty()) {
                wait();
            }
            int v = queue.remove();
            notifyAll();
            return v;
        }
    }

    /** 有界队列：Condition 实现（推荐） */
    static class ConditionQueue {
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition notFull = lock.newCondition();
        private final Condition notEmpty = lock.newCondition();
        private final Queue<Integer> queue = new LinkedList<>();
        private final int capacity = 2;

        void put(int v) throws InterruptedException {
            lock.lock();
            try {
                while (queue.size() == capacity) {
                    notFull.await();
                }
                queue.add(v);
                System.out.println("  [Condition] 放入 " + v + ", 当前 " + queue);
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }

        int take() throws InterruptedException {
            lock.lock();
            try {
                while (queue.isEmpty()) {
                    notEmpty.await();
                }
                int v = queue.remove();
                notFull.signal();
                return v;
            } finally {
                lock.unlock();
            }
        }
    }

    public static void demo() throws InterruptedException {
        // wait/notify 版本
        WaitNotifyQueue q1 = new WaitNotifyQueue();
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    q1.put(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    System.out.println("  [wait/notify] 取出 " + q1.take());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();

        // Condition 版本
        ConditionQueue q2 = new ConditionQueue();
        Thread p2 = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    q2.put(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        Thread c2 = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    System.out.println("  [Condition] 取出 " + q2.take());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        p2.start();
        c2.start();
        p2.join();
        c2.join();
    }
}
