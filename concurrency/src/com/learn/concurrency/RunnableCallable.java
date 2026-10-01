package com.learn.concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Runnable / Callable / Future：三种任务表达方式与结果获取。
 */
public class RunnableCallable {

    public static void demo() throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            // Runnable：无返回值
            Runnable task = () -> System.out.println("  [Runnable] 执行无返回值任务");
            pool.submit(task).get();

            // Callable：有返回值、可抛异常
            Callable<Integer> answer = () -> {
                System.out.println("  [Callable] 计算中...");
                return 40 + 2;
            };
            Future<Integer> future = pool.submit(answer);
            System.out.println("Future.get() = " + future.get());

            // get 带超时，防止无限等待
            Future<String> slow = pool.submit(() -> {
                Thread.sleep(30);
                return "完成";
            });
            try {
                System.out.println("带超时 get: " + slow.get(2, TimeUnit.SECONDS));
            } catch (ExecutionException | TimeoutException e) {
                System.out.println("获取结果失败: " + e);
            }

            // cancel 取消任务
            Future<?> cancelable = pool.submit(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            System.out.println("cancel 返回: " + cancelable.cancel(true));
        } finally {
            pool.shutdownNow();
            pool.awaitTermination(2, TimeUnit.SECONDS);
        }
    }
}
