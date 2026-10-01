package com.learn.generics;

import java.util.Arrays;
import java.util.List;

/**
 * 有界类型参数：&lt;T extends Comparable&lt;T&gt;&gt; 等约束。
 */
public class BoundedTypes {

    /**
     * T 必须实现 Comparable&lt;T&gt;，因此可直接调用 compareTo。
     * 注意：这里 extends 既可以是“类”也可以是“接口”（对类型参数统一用 extends）。
     */
    static <T extends Comparable<T>> T max(List<T> list) {
        T best = list.get(0);
        for (T item : list) {
            if (item.compareTo(best) > 0) {
                best = item;
            }
        }
        return best;
    }

    /** 多重上界：T 必须同时是 Number 的子类且实现 Comparable */
    static <T extends Number & Comparable<T>> T maxOf(T a, T b) {
        return a.compareTo(b) >= 0 ? a : b;
    }

    /** 自定义上界：任何“可序列化为字符串”的实体 */
    interface Stringify {
        String toText();
    }

    static class Product implements Stringify {
        final String name;

        Product(String name) {
            this.name = name;
        }

        @Override
        public String toText() {
            return "Product:" + name;
        }
    }

    static <T extends Stringify> String describe(T item) {
        return item.toText();
    }

    public static void demo() {
        System.out.println("-- <T extends Comparable<T>> 求最大值 --");
        System.out.println("max(整数) = " + max(List.of(3, 9, 4)));
        System.out.println("max(字符串) = " + max(List.of("apple", "pear", "banana")));

        System.out.println("-- 多重上界 <T extends Number & Comparable<T>> --");
        System.out.println("maxOf(3.5, 2.1) = " + maxOf(3.5, 2.1));

        System.out.println("-- 自定义上界 --");
        System.out.println(describe(new Product("键盘")));

        System.out.println("-- 小结：有界参数让泛型类型可以安全调用上界的方法 --");
        System.out.println(Arrays.toString(new int[] { 1, 2, 3 }));
    }
}
