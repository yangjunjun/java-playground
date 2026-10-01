package com.learn.generics;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 类型擦除：泛型信息只存在于编译期，运行时被擦除及其后果。
 */
public class TypeErasure {

    /** 泛型父类：store 的形参是类型参数 T */
    static class Store<T> {
        void store(T item) {
            System.out.println("Store.store(" + item + ")");
        }
    }

    /**
     * 重写带泛型的方法时，编译器生成“桥接方法”（bridge method）保持多态。
     * 编译后等价于：store(Object obj) { store((String) obj); }
     */
    static class UpperStore extends Store<String> {
        @Override
        void store(String s) {
            super.store(s == null ? null : s.toUpperCase());
        }
    }

    static class PairBox<A, B> {
        final A a;
        final B b;

        PairBox(A a, B b) {
            this.a = a;
            this.b = b;
        }
    }

    public static void demo() {
        System.out.println("-- 擦除：运行时 List<String> 与 List<Integer> 同一 Class --");
        List<String> s1 = new ArrayList<>();
        List<Integer> s2 = new ArrayList<>();
        System.out.println("List<String>.class == List<Integer>.class ? "
                + (s1.getClass() == s2.getClass()));

        System.out.println("-- 后果1：不能 new T[] --");
        System.out.println("（见源码注释）new T[] 编译错误，只能 (T[]) new Object[n] 或用 List<T>");

        System.out.println("-- 后果2：不能 instanceof List<String> --");
        List<?> raw = List.of("x");
        System.out.println("raw instanceof List<?> ? " + (raw instanceof List<?>));
        System.out.println("raw instanceof List<String> 是编译错误（只能擦到 List 或 List<?>）");

        System.out.println("-- 后果3：桥接方法 --");
        for (Method m : UpperStore.class.getDeclaredMethods()) {
            System.out.println("  " + m.getName() + "(" + m.getParameterTypes()[0].getSimpleName() + ")");
        }
        Store<String> store = new UpperStore();
        store.store("hello"); // 动态分派经桥接方法（store(Object)）进入 UpperStore

        System.out.println("-- PairBox<A,B> 运行时与 PairBox 无泛型信息 --");
        PairBox<String, Integer> p = new PairBox<>("id", 1);
        System.out.println("p = " + p.a + ", " + p.b + "；运行时只有 raw PairBox");
    }
}
