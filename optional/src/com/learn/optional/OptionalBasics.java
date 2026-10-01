package com.learn.optional;

import java.util.Map;
import java.util.Optional;

/**
 * Optional 基础：为什么需要 Optional，与 null 的对比。
 */
public class OptionalBasics {

    // 传统 null 写法：调用方必须时刻提防空指针
    static String findUserNameOrNull(Map<String, String> db, String id) {
        return db.get(id); // 可能为 null
    }

    // Optional 写法：类型系统明确表达“可能没有值”
    static Optional<String> findUserName(Map<String, String> db, String id) {
        return Optional.ofNullable(db.get(id));
    }

    public static void demo() {
        Map<String, String> db = Map.of("1", "张三");

        // null 写法：链式调用容易 NPE
        String name = findUserNameOrNull(db, "2");
        String upper = name == null ? "未知" : name.toUpperCase();
        System.out.println("null 防御式写法: " + upper);

        // Optional 写法：意图清晰，编译器也帮不上忙的地方至少读起来明确
        String result = findUserName(db, "2")
                .map(String::toUpperCase)
                .orElse("未知");
        System.out.println("Optional 写法: " + result);

        System.out.println("Optional 是“可能无值”的容器，不是序列化方案，也不是 null 的完全替代");
    }
}
