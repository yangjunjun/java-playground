package com.learn.generics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 泛型陷阱：原始类型、泛型数组、无法实例化类型参数等。
 */
public class GenericPitfalls {

    static class Box<T> {
        private T content;

        void put(T item) {
            this.content = item;
        }

        T get() {
            return content;
        }
    }

    /** 不要返回 null 当“空集合”——用 Collections.emptyList() / List.of() */
    static <T> List<T> neverReturnNull() {
        return List.of();
    }

    public static void demo() {
        System.out.println("-- 陷阱1：原始类型（raw type）绕过检查 --");
        List<String> typed = new ArrayList<>();
        typed.add("安全");
        // List raw = typed; raw.add(123);   // 不报错，但破坏了类型约束
        // String s = typed.get(1);          // 运行时 ClassCastException
        System.out.println("（源码注释演示）raw.add(123) 不编译报错，却在 get 强转时炸掉");

        System.out.println("-- 陷阱2：不能创建泛型数组 --");
        // Box<String>[] arr = new Box<String>[10]; // 编译错误
        List<Box<String>> list = new ArrayList<>(); // 用集合代替数组
        list.add(new Box<>());
        System.out.println("改用 List<Box<String>>，size = " + list.size());

        System.out.println("-- 陷阱3：不能实例化类型参数 T --");
        // T t = new T(); // 编译错误
        // new T[10];     // 编译错误
        System.out.println("需要工厂：传 Supplier<T>，如 Supplier<Box> s = Box::new");

        System.out.println("-- 陷阱4：基本类型不能做类型参数 --");
        // List<int> bad = ...;   // 编译错误
        List<Integer> good = List.of(1, 2, 3);
        System.out.println("List<Integer> = " + good + "（自动装箱有开销）");

        System.out.println("-- 陷阱5：不同泛型类型之间没有继承关系 --");
        // List<Object> objs = new ArrayList<String>(); // 编译错误
        List<? extends Object> ok = new ArrayList<String>(); // 通配符可以
        System.out.println("List<String> 不是 List<Object> 的子类；可用 ? extends Object / ? 接收");

        System.out.println("-- 好习惯：返回空集合而不是 null --");
        System.out.println("neverReturnNull() = " + neverReturnNull() + "（元素数 " + neverReturnNull().size() + "）");
        System.out.println("Arrays 工具与不可变列表示例 = " + Arrays.asList("只读视图", "固定大小"));
    }
}
