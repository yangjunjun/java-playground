package com.learn.optional;

import java.util.Optional;

/**
 * Optional 的取值：orElse / orElseGet / orElseThrow / ifPresent。
 */
public class OptionalOrElse {

    static String computeDefault() {
        System.out.println("  （计算默认值，有副作用）");
        return "默认值";
    }

    public static void demo() {
        Optional<String> present = Optional.of("值");
        Optional<String> empty = Optional.empty();

        // orElse：参数总是被求值，无论有没有值
        System.out.println("orElse 有值: " + present.orElse("默认"));
        System.out.println("orElse 无值: " + empty.orElse("默认"));
        System.out.println("orElse 的默认值总是被计算：");
        empty.orElse(computeDefault());
        present.orElse(computeDefault()); // 注意：这里也会打印副作用

        // orElseGet：惰性求值，仅在 empty 时调用 Supplier
        System.out.println("orElseGet 有值（不触发副作用）: "
                + present.orElseGet(() -> {
                    computeDefault();
                    return "默认";
                }));
        System.out.println("orElseGet 无值（触发副作用）: "
                + empty.orElseGet(() -> {
                    computeDefault();
                    return "默认";
                }));

        // orElseThrow：无值直接抛异常
        System.out.println("orElseThrow 有值: " + present.orElseThrow());
        try {
            empty.orElseThrow(() -> new IllegalStateException("没有值"));
        } catch (IllegalStateException e) {
            System.out.println("orElseThrow 无值: " + e.getMessage());
        }

        // ifPresent / ifPresentOrElse（Java 9+）
        present.ifPresent(v -> System.out.println("ifPresent: " + v));
        empty.ifPresentOrElse(
                v -> System.out.println("不该到这里"),
                () -> System.out.println("ifPresentOrElse 走默认分支"));

        // stream()（Java 9+）：Optional 变成 0/1 元素的流
        System.out.println("stream() 计数: " + empty.stream().count());

        // isEmpty（Java 11+）
        System.out.println("isEmpty（Java 11+）: " + empty.isEmpty());
    }
}
