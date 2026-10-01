package com.learn.vthreads;

import java.lang.ScopedValue;

/**
 * 作用域值 ScopedValue（Java 21 预览 → Java 25 正式）。
 * 在某个作用域内把值绑定给线程（含子线程），作用域结束后自动失效。
 * 比 ThreadLocal 更安全：不可变绑定、自动清理、可被子线程继承，不会内存泄漏。
 */
public class ScopedValuesDemo {

    // 声明一个作用域值键（推荐用 static final）
    private static final ScopedValue<String> USER = ScopedValue.newInstance();
    private static final ScopedValue<String> TRACE_ID = ScopedValue.newInstance();

    public static void demo() throws Exception {
        // ---- 1. 基本绑定：where(...).run(...) ----
        ScopedValue.where(USER, "alice").run(() -> {
            System.out.println("  作用域内 USER=" + USER.get());
            inner();
        });
        // 出了作用域就取不到
        System.out.println("  作用域外 isBound=" + USER.isBound() + ", orElse=" + USER.orElse("匿名"));

        // ---- 2. 多值链式绑定 ----
        ScopedValue.where(USER, "bob")
                .where(TRACE_ID, "trace-123")
                .run(() -> {
                    System.out.println("  多值绑定: USER=" + USER.get() + ", TRACE_ID=" + TRACE_ID.get());
                });

        // ---- 3. 线程继承：注意，普通 Thread（含虚拟线程）默认【不】继承 ----
        // ScopedValue 的绑定只在结构化并发（StructuredTaskScope.fork）中被子任务继承；
        // plain Thread.start() 出去的线程读不到。见下方演示：
        ScopedValue.where(USER, "carol").run(() -> {
            var t = Thread.ofVirtual().start(() -> {
                System.out.println("  普通虚拟线程【不继承】: USER=" + USER.orElse("（读不到，符合预期）"));
            });
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("  （继承仅发生在 StructuredTaskScope.fork 的子任务上，见 README）");
        });

        // ---- 4. call() 带返回值 ----
        Integer len = ScopedValue.where(USER, "dave").call(() -> USER.get().length());
        System.out.println("  call() 返回值: " + len);

        // ---- 5. 对比 ThreadLocal 的问题 ----
        // ThreadLocal 用完不 remove 会泄漏（尤其线程池复用线程）；
        // ThreadLocal 不会自动被虚拟线程继承（需 withInitial / 手动传递）。
        ThreadLocal<String> tl = new ThreadLocal<>();
        tl.set("旧写法");
        System.out.println("  ThreadLocal 取值: " + tl.get());
        tl.remove(); // 必须手动清理，否则泄漏
        System.out.println("  ThreadLocal 已手动 remove，ScopedValue 则自动失效");

        System.out.println("小结：优先 ScopedValue（不可变、自动清理、可继承），少用 ThreadLocal");
    }

    private static void inner() {
        // 嵌套调用仍能读到绑定
        System.out.println("  嵌套方法读取 USER=" + USER.get());
    }
}
