package com.learn.concurrency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * ConcurrentHashMap：线程安全 Map；compute 原子更新。
 * 对比：HashMap 并发 put 可能死循环/丢数据。
 */
public class ConcurrentHashMapDemo {

    public static void demo() throws InterruptedException {
        // 1) ConcurrentHashMap + compute：原子计数
        ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<>();
        ExecutorService pool = Executors.newFixedThreadPool(4);
        try {
            for (int i = 0; i < 4; i++) {
                pool.submit(() -> {
                    for (int j = 0; j < 1000; j++) {
                        counts.compute("hits", (k, v) -> v == null ? 1 : v + 1);
                    }
                });
            }
        } finally {
            pool.shutdown();
            pool.awaitTermination(5, TimeUnit.SECONDS);
        }
        System.out.println("compute 原子计数: " + counts.get("hits") + "（期望 4000）");

        // 2) putIfAbsent / merge
        ConcurrentHashMap<String, List<String>> logins = new ConcurrentHashMap<>();
        logins.computeIfAbsent("u1", k -> new ArrayList<>()).add("登录");
        logins.merge("u1", List.of("下单"), (oldV, newV) -> {
            List<String> merged = new ArrayList<>(oldV);
            merged.addAll(newV);
            return merged;
        });
        System.out.println("computeIfAbsent + merge: " + logins.get("u1"));

        // 3) 反面教材：HashMap 并发 put/compute 会丢更新甚至抛异常，不要在并发场景用
        Map<String, Integer> unsafe = new HashMap<>();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            threads.add(new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    unsafe.compute("k", (k, v) -> v == null ? 1 : v + 1);
                }
            }));
        }
        try {
            threads.forEach(Thread::start);
            for (Thread t : threads) {
                t.join();
            }
            System.out.println("HashMap 并发 compute: " + unsafe.get("k")
                    + "（通常小于 4000，甚至抛异常——不要这样用）");
        } catch (Throwable ex) {
            System.out.println("HashMap 并发 compute 出现异常: " + ex
                    + "（这正是不该用 HashMap 做并发的原因）");
        }
    }
}
