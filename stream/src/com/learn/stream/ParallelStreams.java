package com.learn.stream;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 并行流：适用场景与线程安全隐患。
 */
public class ParallelStreams {

    public static void demo() {
        List<Integer> numbers = IntStream.rangeClosed(1, 1000).boxed().collect(Collectors.toList());

        // 并行流利用 ForkJoinPool，适合大数据、无状态、无副作用的计算
        long parallelSum = numbers.parallelStream().mapToLong(Integer::longValue).sum();
        System.out.println("并行求和: " + parallelSum);

        // parallelStream 的 forEach 不保证顺序；forEachOrdered 保序但会牺牲并行收益
        System.out.print("parallel forEach 顺序可能乱: ");
        numbers.parallelStream().limit(5).forEach(n -> System.out.print(n + " "));
        System.out.println();

        // 不安全：共享可变状态会出现丢更新 / ArrayIndexOutOfBoundsException
        // List<Integer> shared = new ArrayList<>();
        // numbers.parallelStream().forEach(shared::add); // 千万别这么写
        List<Integer> safe = numbers.parallelStream().collect(Collectors.toList());
        System.out.println("并行收集后大小（安全）: " + safe.size());

        System.out.println("并行适用：大数据量 + 无状态 + 无副作用；否则老实串行");
    }
}
