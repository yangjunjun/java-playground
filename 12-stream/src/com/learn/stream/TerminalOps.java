package com.learn.stream;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * 终止操作：forEach / reduce / 统计 / 查找匹配 / 短路。
 */
public class TerminalOps {

    public static void demo() {
        List<Integer> numbers = List.of(3, 1, 4, 1, 5, 9, 2, 6);

        // forEach 不保证顺序，forEachOrdered 在并行流中仍保序
        System.out.print("forEach: ");
        numbers.stream().forEach(n -> System.out.print(n + " "));
        System.out.println();
        System.out.print("forEachOrdered（并行）: ");
        numbers.parallelStream().forEachOrdered(n -> System.out.print(n + " "));
        System.out.println();

        // mapToInt + summaryStatistics：一次遍历拿到 count/sum/min/max/avg
        IntSummaryStatistics stats = numbers.stream()
                .mapToInt(Integer::intValue)
                .summaryStatistics();
        System.out.println("统计: count=" + stats.getCount()
                + " sum=" + stats.getSum()
                + " min=" + stats.getMin()
                + " max=" + stats.getMax()
                + " avg=" + stats.getAverage());

        // reduce：折叠
        int sum = numbers.stream().reduce(0, Integer::sum);
        Optional<Integer> max = numbers.stream().reduce(Integer::max);
        System.out.println("reduce 求和: " + sum + "，reduce 求最大: " + max.orElse(0));

        // 短路操作
        System.out.println("anyMatch >8: " + numbers.stream().anyMatch(n -> n > 8));
        System.out.println("allMatch > 0: " + numbers.stream().allMatch(n -> n > 0));
        System.out.println("noneMatch < 0: " + numbers.stream().noneMatch(n -> n < 0));
        System.out.println("findFirst: " + numbers.stream().findFirst().orElse(null));
        System.out.println("findAny: " + numbers.parallelStream().findAny().orElse(null));
        System.out.println("min / max: " + numbers.stream().min(Integer::compareTo)
                + " / " + numbers.stream().max(Integer::compareTo));
        System.out.println("count 奇数个数: " + numbers.stream().filter(n -> n % 2 == 1).count());
    }
}
