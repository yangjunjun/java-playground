package com.learn.generics;

import java.util.List;

/**
 * 泛型方法：独立于类的类型参数、类型推断、@SafeVarargs 与 List.of。
 */
public class GenericMethod {

    /** 泛型方法：<T> 写在返回值前；T 只在本方法内有效 */
    static <T> T firstOf(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }

    static <T> void swap(T[] arr, int i, int j) {
        T tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    /** varargs + 泛型：编译器可能告警，@SafeVarargs 声明不做“不安全”操作 */
    @SafeVarargs
    static <T> List<T> asList(T... items) {
        // 只读 items（不往数组里塞其他类型），因此是安全的
        return List.of(items);
    }

    public static void demo() {
        System.out.println("-- 泛型方法 + 类型推断 --");
        // 不用显式写 Main.<String>firstOf(...)，编译器从实参推断 T=String
        String first = firstOf(List.of("甲", "乙", "丙"));
        System.out.println("firstOf(...) = " + first);

        Integer firstInt = firstOf(List.of(10, 20, 30));
        System.out.println("firstOf(...) = " + firstInt);

        System.out.println("-- 泛型数组交换 --");
        String[] arr = { "A", "B", "C" };
        swap(arr, 0, 2);
        System.out.println("swap 后 = " + java.util.Arrays.toString(arr));

        System.out.println("-- @SafeVarargs 构造不可变列表 --");
        List<String> words = asList("一", "二", "三");
        System.out.println("asList(...) = " + words);

        System.out.println("-- List.of（Java 9+）：不可变、拒绝 null --");
        List<Integer> nums = List.of(1, 2, 3);
        System.out.println("List.of(1,2,3) = " + nums);
        try {
            List.of(1, null);
        } catch (NullPointerException e) {
            System.out.println("List.of 不接受 null（抛出 NullPointerException）");
        }
    }
}
