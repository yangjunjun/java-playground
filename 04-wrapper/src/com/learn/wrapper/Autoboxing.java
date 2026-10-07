package com.learn.wrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动装箱与拆箱（Java 5+）：编译器插入 valueOf / xxxValue，null 拆箱抛 NPE。
 */
public class Autoboxing {

    public static void demo() {
        Integer i = 100;
        int n = i;
        System.out.println("装箱 Integer i = 100 → 实际调用 Integer.valueOf(100)");
        System.out.println("拆箱 int n = i → 实际调用 i.intValue(), n=" + n);

        Long l = 200L; // int 字面量装箱成 Long 会编译失败，须是 long 字面量或显式 (long) 转换
        long raw = l;
        System.out.println("Long 同理: raw=" + raw);

        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        int first = list.get(0);
        // 集合元素是包装类，取出时自动拆箱
        System.out.println("集合元素装箱/拆箱: list=" + list + ", get(0) 拆箱后=" + first);

        Long mixed = 5L;
        long widened = mixed;
        System.out.println("Long 赋给 long 再加宽: " + widened);

        nullUnboxing();
    }

    private static void nullUnboxing() {
        Integer maybeNull = null;
        try {
            // 包装类可能是 null，拆箱瞬间 NPE
            int oops = maybeNull;
            System.out.println("不应执行: " + oops);
        } catch (NullPointerException e) {
            System.out.println("拆箱 null 抛出: " + e.getClass().getSimpleName()
                    + "（取值前先判空）");
        }

        int safe = maybeNull == null ? 0 : maybeNull;
        System.out.println("安全写法（判空 + 默认值）: " + safe);
    }
}
