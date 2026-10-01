package com.learn.stream;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Stream 的创建方式。
 */
public class StreamCreation {

    public static void demo() {
        // Stream.of
        Stream<String> of = Stream.of("a", "b", "c");
        System.out.println("Stream.of: " + of.collect(Collectors.toList()));

        // Collection.stream()
        List<Integer> list = List.of(1, 2, 3);
        System.out.println("list.stream: " + list.stream().map(i -> i * 10).collect(Collectors.toList()));

        // Arrays.stream
        int[] nums = {1, 2, 3, 4};
        System.out.println("Arrays.stream sum: " + Arrays.stream(nums).sum());

        // Stream.generate：无限流，必须 limit 截断
        Stream<String> gen = Stream.generate(() -> "tick").limit(3);
        System.out.println("Stream.generate: " + gen.collect(Collectors.toList()));

        // Stream.iterate：无限流
        Stream<Integer> iter = Stream.iterate(1, n -> n * 2).limit(5);
        System.out.println("Stream.iterate: " + iter.collect(Collectors.toList()));

        // Stream.concat
        Stream<String> concat = Stream.concat(Stream.of("x"), Stream.of("y", "z"));
        System.out.println("Stream.concat: " + concat.collect(Collectors.toList()));

        // IntStream.range / rangeClosed
        System.out.println("IntStream.range(1,5): "
                + IntStream.range(1, 5).boxed().collect(Collectors.toList()));
        System.out.println("IntStream.rangeClosed(1,5): "
                + IntStream.rangeClosed(1, 5).boxed().collect(Collectors.toList()));

        // Stream.builder
        Stream<String> built = Stream.<String>builder().add("p").add("q").build();
        System.out.println("Stream.builder: " + built.collect(Collectors.toList()));
    }
}
