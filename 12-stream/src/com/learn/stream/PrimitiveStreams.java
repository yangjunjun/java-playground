package com.learn.stream;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 原始类型流：IntStream / LongStream / DoubleStream。
 */
public class PrimitiveStreams {

    public static void demo() {
        // 常用统计
        System.out.println("IntStream.sum: " + IntStream.of(3, 1, 4, 1, 5, 9).sum());
        System.out.println("average: " + IntStream.of(3, 1, 4).average().orElse(0));
        System.out.println("max: " + IntStream.of(3, 1, 4).max().orElse(0));

        // range / rangeClosed
        System.out.println("range(0,5): " + IntStream.range(0, 5).boxed().collect(Collectors.toList()));

        // boxed：IntStream -> Stream<Integer>
        Stream<Integer> boxed = IntStream.rangeClosed(1, 3).boxed();
        System.out.println("boxed: " + boxed.collect(Collectors.toList()));

        // mapToObj / mapToLong / mapToDouble
        List<String> labels = IntStream.rangeClosed(1, 3)
                .mapToObj(i -> "第" + i + "名")
                .collect(Collectors.toList());
        System.out.println("mapToObj: " + labels);

        // DoubleStream 与 summaryStatistics
        DoubleSummaryStatistics ds = DoubleStream.of(1.5, 2.5, 3.5).summaryStatistics();
        System.out.println("DoubleSummaryStatistics: count=" + ds.getCount() + " avg=" + ds.getAverage());

        // 无限流要 limit
        System.out.println("generate: " + IntStream.generate(() -> 7).limit(3).boxed()
                .collect(Collectors.toList()));
        System.out.println("iterate: " + IntStream.iterate(0, n -> n + 2).limit(5).boxed()
                .collect(Collectors.toList()));

        System.out.println("原始类型流避免自动装箱，统计场景性能更好");
    }
}
