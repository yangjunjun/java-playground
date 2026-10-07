package com.learn.lambda;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * 方法引用的四种形式：静态、无界实例、有界实例、构造器。
 */
public class MethodReferences {

    static class Person {
        final String name;
        final int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return name + "(" + age + ")";
        }
    }

    public static String shout(String s) {
        return s.toUpperCase() + "!";
    }

    public static void demo() {
        // 1) 静态方法引用：ClassName::staticMethod
        UnaryOperator<String> shout = MethodReferences::shout;
        System.out.println("静态方法引用: " + shout.apply("hi"));

        // 2) 无界实例方法引用（特定类）：ClassName::instanceMethod
        //    等价于 s -> s.toUpperCase()，第一个参数作接收者
        Function<String, String> upper = String::toUpperCase;
        System.out.println("无界实例方法引用: " + upper.apply("ok"));

        // 3) 有界实例方法引用：object::instanceMethod
        String prefix = "ID-";
        Function<String, String> bound = prefix::concat;
        System.out.println("有界实例方法引用: " + bound.apply("42"));

        // 4) 构造器引用：ClassName::new
        Supplier<ArrayList<String>> ctor = ArrayList::new;
        List<String> made = ctor.get();
        made.add("生成的列表");
        System.out.println("无参构造器引用: " + made);

        BiFunction<String, Integer, Person> personCtor = Person::new;
        System.out.println("带参构造器引用: " + personCtor.apply("小明", 18));

        // 方法引用常与 Comparator / Stream 搭配
        List<String> words = new ArrayList<>(List.of("pear", "apple", "fig"));
        words.sort(Comparator.comparingInt(String::length));
        System.out.println("comparingInt(String::length): " + words);
    }
}
