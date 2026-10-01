package com.learn.optional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Optional 常见使用模式（含 Stream 集成）。
 */
public class OptionalPatterns {

    static class Item {
        final String category;
        final String tag; // 可能为 null

        Item(String category, String tag) {
            this.category = category;
            this.tag = tag;
        }
    }

    public static void demo() {
        // 模式 1：链式转换 + 兜底
        String upper = Optional.of("  hello  ")
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .orElse("空");
        System.out.println("链式转换: " + upper);

        // 模式 2：Optional 与 Stream 集成，用 Optional::stream 摊平
        List<Optional<String>> opts = List.of(Optional.of("a"), Optional.empty(), Optional.of("b"));
        List<String> collected = opts.stream()
                .flatMap(Optional::stream) // Java 9+
                .collect(Collectors.toList());
        System.out.println("flatMap(Optional::stream): " + collected);

        // 模式 3：Collectors.flatMapping（Java 9+）—— 把可空字段收集成集合
        List<Item> items = List.of(
                new Item("书", "技术"),
                new Item("书", null),
                new Item("笔", "文具"));
        Map<String, Set<String>> tagsByCategory = items.stream()
                .collect(Collectors.groupingBy(
                        i -> i.category,
                        Collectors.flatMapping(
                                i -> Optional.ofNullable(i.tag).stream(),
                                Collectors.toSet())));
        System.out.println("flatMapping: " + tagsByCategory);

        // 模式 4：orElseThrow 提前失败
        String must = Optional.ofNullable(System.getenv("PATH"))
                .map(p -> "PATH 已设置")
                .orElseThrow();
        System.out.println("orElseThrow: " + must);
    }
}
