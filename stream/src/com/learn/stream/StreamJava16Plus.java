package com.learn.stream;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Java 16+ 增强：Stream.toList() 与 mapMulti。
 */
public class StreamJava16Plus {

    public static void demo() {
        // Stream.toList()（Java 16+）：返回不可修改的 List，且不允许 null 元素
        List<String> list = Stream.of("a", "b", "c").toList();
        System.out.println("Stream.toList(): " + list);
        try {
            list.add("d");
        } catch (UnsupportedOperationException e) {
            System.out.println("toList() 不可修改: " + e.getClass().getSimpleName());
        }

        // 对比 Collectors.toList()：返回可变的 ArrayList
        List<String> mutable = Stream.of("a", "b").collect(Collectors.toList());
        mutable.add("c");
        System.out.println("Collectors.toList() 可修改: " + mutable);

        // Collectors.toUnmodifiableList()（Java 10+）同样不可变；只读场景优先用 Stream.toList()
        System.out.println("只读场景推荐 Stream.toList()，需要可变结果用 Collectors.toList()");

        // mapMulti（Java 16+）：flatMap 的替代，可减少中间 Stream 对象
        List<Integer> expanded = Stream.of(1, 2, 3)
                .<Integer>mapMulti((n, consumer) -> {
                    consumer.accept(n);
                    consumer.accept(n * 2);
                })
                .toList();
        System.out.println("mapMulti: " + expanded);

        // Stream.ofNullable（Java 9+）：null 变成空流
        System.out.println("Stream.ofNullable(null) count: " + Stream.ofNullable(null).count());
    }
}
