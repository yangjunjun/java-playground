package com.learn.lambda;

import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * 函数式组合：andThen / compose / and / or / negate。
 */
public class FunctionalComposition {

    public static void demo() {
        Function<Integer, Integer> doubleIt = x -> x * 2;
        Function<Integer, Integer> plusOne = x -> x + 1;

        // andThen：先执行自身，再执行参数（从左到右）
        System.out.println("doubleIt.andThen(plusOne) 5 = " + doubleIt.andThen(plusOne).apply(5)); // 11
        // compose：先执行参数，再执行自身
        System.out.println("doubleIt.compose(plusOne) 5 = " + doubleIt.compose(plusOne).apply(5)); // 12

        Predicate<String> hasA = s -> s.contains("a");
        Predicate<String> hasB = s -> s.contains("b");
        System.out.println("and: " + hasA.and(hasB).test("abc"));
        System.out.println("or: " + hasA.or(hasB).test("xyz"));
        System.out.println("negate: " + hasA.negate().test("xyz"));

        UnaryOperator<String> trim = String::trim;
        UnaryOperator<String> upper = String::toUpperCase;
        // 注意：Function.andThen 的返回类型是 Function，不是 UnaryOperator
        Function<String, String> pipeline = trim.andThen(upper);
        System.out.println("pipeline: " + pipeline.apply("  hello  "));

        IntUnaryOperator addThenSquare = ((IntUnaryOperator) x -> x + 1).andThen(x -> x * x);
        System.out.println("IntUnaryOperator 组合: " + addThenSquare.applyAsInt(3)); // 16
    }
}
