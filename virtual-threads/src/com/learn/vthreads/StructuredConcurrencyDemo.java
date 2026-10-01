package com.learn.vthreads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 结构化并发 StructuredTaskScope —— 【预览 API，本示例不启用】。
 *
 * java.util.concurrent.StructuredTaskScope 在 JDK 25 中仍是预览特性，
 * 编译/运行需要 --enable-preview。为保证本模块在原生 JDK 25 上直接跑通，
 * 这里实现一个「纯虚拟线程」的等价替代：用 CompletableFuture 组合子任务，
 * 语义接近 shutdown-on-failure（任一失败即取消其余）。
 *
 * 完整 StructuredTaskScope（Joiner）写法见 README.md 代码块。
 */
public class StructuredConcurrencyDemo {

    private static String fetch(String name) throws Exception {
        Thread.sleep(80); // 模拟 IO
        if ("bad".equals(name)) {
            throw new IllegalStateException(name + " 服务故障");
        }
        return name + "-数据";
    }

    public static void demo() throws Exception {
        System.out.println("说明: StructuredTaskScope 是 JDK 25 预览 API（需 --enable-preview），");
        System.out.println("      此处用虚拟线程 + CompletableFuture 演示等价语义。");

        // ---- 1. 汇总全部成功结果（等价 Joiner.allSuccessfulOrThrow）----
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> u = exec.submit(() -> fetch("user"));
            Future<String> o = exec.submit(() -> fetch("order"));
            System.out.println("  全部成功: user=" + u.get() + ", order=" + o.get());
        }

        // ---- 2. shutdown-on-failure 语义（任一失败 → 取消其余）----
        // 等价 Joiner.awaitAllSuccessfulOrThrow()
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<String>> futures = new ArrayList<>();
            for (String name : List.of("user", "bad", "order")) {
                futures.add(CompletableFuture.supplyAsync(() -> {
                    try {
                        return fetch(name);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, exec));
            }
            // anyOf 失败就立刻抛，随后 cancel 其余
            try {
                CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                        .exceptionally(ex -> null) // 汇总异常
                        .join();
                // 手动检查是否任一失败
                for (var f : futures) {
                    f.join(); // 失败的会抛
                }
                System.out.println("  不应到达");
            } catch (Exception e) {
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                System.out.println("  shutdown-on-failure: " + cause.getClass().getSimpleName()
                        + " -> " + cause.getMessage());
                futures.forEach(f -> f.cancel(true)); // 取消未完成的
            }
        }

        // ---- 3. 取第一个成功结果（等价 Joiner.anySuccessfulResultOrThrow）----
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<String> slow = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(150);
                    return "慢节点";
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return "中断";
                }
            }, exec);
            CompletableFuture<String> fast = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(30);
                    return "快节点";
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return "中断";
                }
            }, exec);
            String first = slow.applyToEither(fast, s -> s).join();
            System.out.println("  第一个成功结果: " + first);
            slow.cancel(true);
            fast.cancel(true);
        }

        // ---- 对比：手动 Future 写法的痛点 ----
        System.out.println("对比: Future 写法需手动 shutdown、异常传播繁琐、任务可能泄漏");
        System.out.println("      StructuredTaskScope 用 try-with-resources 保证子任务一定结束");

        System.out.println("小结: 结构化并发保证「进入作用域的任务一定在退出前结束」，异常自动传播");
    }
}
