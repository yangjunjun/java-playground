package com.learn.optional;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Optional 的常见反模式与易错点。
 */
public class OptionalPitfalls {

    // 反模式：把 Optional 当字段类型 —— 不要这样做
    // static class Bad { Optional<String> name; }

    // 反模式：把 Optional 当方法参数类型 —— 不要这样做
    // static void bad(Optional<String> name) {}

    // 正常写法：没有值就用 null，或拆成两个方法 / 抛异常
    static class Good {
        String name;
    }

    public static void demo() {
        Optional<String> maybe = Optional.ofNullable(null);

        // 反模式 1：不加判断直接 get()，empty 时抛 NoSuchElementException
        try {
            maybe.get();
        } catch (NoSuchElementException e) {
            System.out.println("直接 get() 抛异常: " + e.getClass().getSimpleName());
        }

        // 正确：isPresent + get，或 orElse / orElseGet / ifPresent
        if (maybe.isPresent()) {
            System.out.println(maybe.get());
        } else {
            System.out.println("isPresent 判断后再 get()");
        }

        // 反模式 2：用 orElse 而不是 orElseGet 处理昂贵的默认值（默认值总是被计算）
        // 反模式 3：Optional.of(x) 包裹已知非 null 的值（多此一举）
        // 反模式 4：Optional 当集合用 —— 需要 0..N 个值就用 List / Stream
        // 反模式 5：Optional 当序列化字段 / JPA 实体字段 / DTO 字段（框架不支持）

        System.out.println("总结：Optional 只适合做返回类型，表达“调用结果可能无值”");
    }
}
