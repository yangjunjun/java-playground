package com.learn.lambda;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * java.util.function 常用函数式接口，以及在集合中的典型应用。
 */
public class BuiltInFunctionals {

    public static void demo() {
        // Function<T,R>：T -> R
        Function<String, Integer> length = String::length;
        System.out.println("Function: " + length.apply("lambda"));

        // BiFunction<T,U,R>：(T,U) -> R
        BiFunction<String, String, String> join = (a, b) -> a + " & " + b;
        System.out.println("BiFunction: " + join.apply("Java", "Kotlin"));

        // Predicate<T>：T -> boolean
        Predicate<Integer> even = n -> n % 2 == 0;
        System.out.println("Predicate: " + even.test(4));

        // Supplier<T>：() -> T
        Supplier<List<String>> factory = ArrayList::new;
        System.out.println("Supplier: " + factory.get());

        // Consumer<T>：T -> void
        Consumer<String> print = s -> System.out.println("Consumer: " + s);
        print.accept("你好");

        // BiConsumer<T,U>：(T,U) -> void
        BiConsumer<String, Integer> kv = (k, v) -> System.out.println("BiConsumer: " + k + "=" + v);
        kv.accept("age", 18);

        // UnaryOperator<T>：T -> T；BinaryOperator<T>：(T,T) -> T
        UnaryOperator<String> bang = s -> s + "!";
        BinaryOperator<Integer> sum = Integer::sum;
        System.out.println("UnaryOperator: " + bang.apply("hi") + "，BinaryOperator: " + sum.apply(1, 2));

        // 原始类型特化版，避免自动装箱
        IntFunction<String> repeat = n -> "*".repeat(n);
        IntUnaryOperator square = n -> n * n;
        System.out.println("IntFunction: " + repeat.apply(4) + "，IntUnaryOperator: " + square.applyAsInt(9));

        // --- 集合中的典型用法 ---
        List<String> names = new ArrayList<>(List.of("Charlie", "Alice", "Bob"));
        names.sort(Comparator.comparing(String::length).thenComparing(Comparator.naturalOrder()));
        System.out.println("Comparator.comparing: " + names);

        names.removeIf(s -> s.startsWith("B"));
        System.out.println("removeIf: " + names);

        Map<String, List<String>> groups = new HashMap<>();
        groups.computeIfAbsent("fruit", k -> new ArrayList<>()).add("apple");
        groups.computeIfAbsent("fruit", k -> new ArrayList<>()).add("pear");
        System.out.println("computeIfAbsent: " + groups);
    }
}
