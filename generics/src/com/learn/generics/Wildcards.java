package com.learn.generics;

import java.util.ArrayList;
import java.util.List;

/**
 * 通配符：? extends / ? super 与 PECS 原则。
 */
public class Wildcards {

    /** ? extends T（上界通配符）：只能读（协变），不能往里 add 具体元素 */
    static double sumOf(List<? extends Number> list) {
        double sum = 0;
        for (Number n : list) {
            sum += n.doubleValue();
        }
        // list.add(1); // 编译错误：不知道实际是 List<Integer> 还是 List<Double>
        return sum;
    }

    /** ? super T（下界通配符）：可写入 T 及其子类，读出来只能当 Object */
    static void fillInts(List<? super Integer> list) {
        list.add(1);
        list.add(2);
        // Integer i = list.get(0); // 编译错误：元素可能是 List<Object> 或 List<Number>
        Object o = list.get(0);
        System.out.println("  读出为 Object: " + o);
    }

    /** 无界通配符 ?：等价于 List&lt;? extends Object&gt;，只读为 Object */
    static int sizeOf(List<?> list) {
        return list.size();
    }

    public static void demo() {
        System.out.println("-- ? extends Number：生产者，只读 --");
        List<Integer> ints = List.of(1, 2, 3);
        List<Double> doubles = List.of(1.5, 2.5);
        System.out.println("sumOf(List<Integer>) = " + sumOf(ints));
        System.out.println("sumOf(List<Double>) = " + sumOf(doubles));

        System.out.println("-- ? super Integer：消费者，可写 --");
        List<Number> dest = new ArrayList<>();
        fillInts(dest);
        System.out.println("fillInts 后 dest = " + dest);

        System.out.println("-- 无界通配符 ? --");
        System.out.println("sizeOf(...) = " + sizeOf(List.of("a", "b")));

        System.out.println("-- PECS：Producer Extends, Consumer Super --");
        System.out.println("从集合读数据（生产者）用 ? extends T；往集合写数据（消费者）用 ? super T");
    }
}
