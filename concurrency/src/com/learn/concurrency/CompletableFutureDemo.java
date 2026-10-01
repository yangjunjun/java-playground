package com.learn.concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * CompletableFuture：supplyAsync / thenApply / thenCombine / exceptionally / allOf。
 */
public class CompletableFutureDemo {

    public static void demo() throws InterruptedException {
        // supplyAsync + thenApply + thenAccept
        CompletableFuture<Void> cf = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println("  异步计算 21");
                    return 21;
                })
                .thenApply(n -> {
                    System.out.println("  翻倍");
                    return n * 2;
                })
                .thenAccept(v -> System.out.println("  结果 = " + v));
        cf.join();

        // thenCombine：合并两个异步结果
        CompletableFuture<Integer> combined = CompletableFuture
                .supplyAsync(() -> 40)
                .thenCombine(CompletableFuture.supplyAsync(() -> 2), Integer::sum);
        System.out.println("thenCombine = " + combined.join());

        // exceptionally：异常兜底
        CompletableFuture<Integer> risky = CompletableFuture
                .supplyAsync(() -> {
                    if (true) {
                        throw new IllegalStateException("业务异常");
                    }
                    return 1;
                })
                .exceptionally(e -> {
                    System.out.println("  捕获异常: " + e.getCause());
                    return -1;
                });
        System.out.println("exceptionally 兜底值 = " + risky.join());

        // allOf：等待全部完成
        CompletableFuture<String> a = CompletableFuture.supplyAsync(() -> "A");
        CompletableFuture<String> b = CompletableFuture.supplyAsync(() -> "B");
        CompletableFuture<Void> all = CompletableFuture.allOf(a, b);
        try {
            all.get(2, TimeUnit.SECONDS);
            System.out.println("allOf 完成: " + a.join() + ", " + b.join());
        } catch (ExecutionException | java.util.concurrent.TimeoutException e) {
            System.out.println("allOf 失败: " + e);
        }
    }
}
