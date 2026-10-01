package com.learn.stream;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 中间操作：map / filter / flatMap / peek / distinct / sorted / limit / skip。
 */
public class IntermediateOps {

    public static void demo() {
        List<String> words = List.of("hello", "world", "java", "stream", "java");

        System.out.println("map 大写: " + words.stream()
                .map(String::toUpperCase).collect(Collectors.toList()));
        System.out.println("filter 长度>4: " + words.stream()
                .filter(s -> s.length() > 4).collect(Collectors.toList()));
        System.out.println("distinct: " + words.stream()
                .distinct().collect(Collectors.toList()));
        System.out.println("sorted 自然序: " + words.stream()
                .sorted().collect(Collectors.toList()));
        System.out.println("sorted 按长度: " + words.stream()
                .sorted(Comparator.comparingInt(String::length)).collect(Collectors.toList()));
        System.out.println("limit(2): " + words.stream().limit(2).collect(Collectors.toList()));
        System.out.println("skip(3): " + words.stream().skip(3).collect(Collectors.toList()));

        // flatMap：把嵌套结构“摊平”
        List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3, 4), List.of(5));
        System.out.println("flatMap 摊平嵌套: " + nested.stream()
                .flatMap(Collection::stream).collect(Collectors.toList()));

        // flatMap 例 2：句子拆词
        List<String> sentences = List.of("hello world", "java stream");
        System.out.println("flatMap 拆词: " + sentences.stream()
                .flatMap(s -> Arrays.stream(s.split(" ")))
                .collect(Collectors.toList()));

        // peek：调试用，生产代码慎用（应避免副作用）
        words.stream()
                .peek(s -> System.out.println("  看一眼: " + s))
                .limit(2)
                .collect(Collectors.toList());
    }
}
