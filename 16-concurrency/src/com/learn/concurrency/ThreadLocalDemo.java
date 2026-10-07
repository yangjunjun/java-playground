package com.learn.concurrency;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * ThreadLocal：线程私有副本。典型场景：SimpleDateFormat、用户上下文。
 * 线程池中用完必须 remove()，否则内存泄漏/脏数据。
 */
public class ThreadLocalDemo {

    /** 错误示范：共享的 SimpleDateFormat 非线程安全 */
    private static final SimpleDateFormat UNSAFE_FMT = new SimpleDateFormat("yyyy-MM-dd");

    /** 正确写法：每个线程一份格式化器 */
    private static final ThreadLocal<SimpleDateFormat> SAFE_FMT =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

    /** 模拟用户会话上下文 */
    private static final ThreadLocal<String> USER = new ThreadLocal<>();

    public static void demo() throws InterruptedException {
        // 1) 竞态下的 SimpleDateFormat 会解析/格式化错乱
        ExecutorService pool = Executors.newFixedThreadPool(4);
        try {
            for (int i = 0; i < 4; i++) {
                pool.submit(() -> {
                    for (int j = 0; j < 200; j++) {
                        String s = UNSAFE_FMT.format(new Date());
                        if (s.length() != 10) {
                            System.out.println("  [不安全] 格式错乱: " + s);
                        }
                    }
                });
            }
        } finally {
            pool.shutdown();
            pool.awaitTermination(5, TimeUnit.SECONDS);
        }
        System.out.println("共享 SimpleDateFormat 演示结束（错乱情况看输出）");

        // 2) ThreadLocal 保证线程安全
        ExecutorService pool2 = Executors.newFixedThreadPool(2);
        try {
            for (int i = 0; i < 2; i++) {
                final int idx = i;
                pool2.submit(() -> {
                    USER.set("用户-" + idx);
                    String s = SAFE_FMT.get().format(new Date());
                    System.out.println("  " + USER.get() + " 格式化: " + s);
                    USER.remove();          // 用完必须 remove
                    SAFE_FMT.remove();
                });
            }
        } finally {
            pool2.shutdown();
            pool2.awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
