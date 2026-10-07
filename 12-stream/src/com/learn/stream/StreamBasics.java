package com.learn.stream;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stream 基础：与集合的区别、惰性求值与流水线顺序。
 */
public class StreamBasics {

    public static void demo() {
        List<String> words = List.of("apple", "banana", "cherry", "date");

        // 集合是数据容器；Stream 是对数据的计算描述，本身不存数据
        Stream<String> stream = words.stream();
        System.out.println("集合 size = " + words.size() + "；Stream 本身不存数据");
        stream.close();

        // 惰性：中间操作要等终止操作触发才真正执行
        List<String> result = words.stream()
                .peek(s -> System.out.println("  peek 进入流水线: " + s))
                .filter(s -> s.length() > 5)
                .peek(s -> System.out.println("  filter 通过: " + s))
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        System.out.println("结果: " + result);

        // 流只能消费一次：再次使用抛 IllegalStateException
        Stream<String> once = words.stream();
        once.forEach(System.out::println);
        try {
            once.forEach(System.out::println);
        } catch (IllegalStateException e) {
            System.out.println("流已消费: " + e.getClass().getSimpleName());
        }
    }
}
