package com.learn.lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Lambda 语法与目标类型（target typing）。
 */
public class LambdaSyntax {

    interface StringFunc {
        String apply(String s);
    }

    interface IntFunc {
        int apply(int x);
    }

    public static void demo() {
        // 无参 lambda
        Runnable r = () -> System.out.println("无参 lambda");
        r.run();

        // 单参：可省略圆括号；类型可省略（由目标类型推断）
        StringFunc upper = s -> s.toUpperCase();
        System.out.println("单参表达式体: " + upper.apply("hello"));

        // 表达式体与块体
        IntFunc square = x -> x * x;
        System.out.println("表达式体: " + square.apply(7));

        IntFunc absThenAdd = x -> {
            int a = Math.abs(x);
            return a + 1;
        };
        System.out.println("块体: " + absThenAdd.apply(-5));

        // 多参：圆括号不能省；类型同时省略或同时写
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        System.out.println("多参: " + add.apply(2, 3));

        // 目标类型：lambda 本身没有类型，赋给谁就按谁的 SAM 推断
        List<String> names = new ArrayList<>(List.of("bb", "a", "ccc"));
        names.sort((x, y) -> x.length() - y.length()); // 目标类型是 Comparator
        System.out.println("目标类型是 Comparator: " + names);
    }
}
