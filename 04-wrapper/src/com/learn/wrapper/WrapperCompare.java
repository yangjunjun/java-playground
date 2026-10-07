package com.learn.wrapper;

import java.util.Arrays;
import java.util.Comparator;

/**
 * 比较与选型：equals / compare / compareTo，包装类与基本类型的取舍。
 */
public class WrapperCompare {

    public static void demo() {
        Integer a = 200;
        Integer b = 200;
        System.out.println("a==b: " + (a == b) + "（超出缓存，比引用）");
        System.out.println("a.equals(b): " + a.equals(b) + "（比内容）");
        System.out.println("Integer.compare(a,b): " + Integer.compare(a, b) + "（静态比较，null 不安全）");
        System.out.println("a.compareTo(b): " + a.compareTo(b));

        Integer[] nums = { 3, 1, 2 };
        Arrays.sort(nums);
        System.out.println("自然序排序: " + Arrays.toString(nums));
        Arrays.sort(nums, Comparator.reverseOrder());
        System.out.println("Comparator 降序: " + Arrays.toString(nums));

        Double nan = Double.NaN;
        System.out.println("NaN 的 compareTo: " + nan.compareTo(Double.MAX_VALUE)
                + "（NaN 视为大于任何数）");
        System.out.println("Double.compare 对 NaN 的处理与排序一致: "
                + Double.compare(Double.NaN, Double.MAX_VALUE));

        Integer maybeNull = null;
        try {
            Integer.compare(maybeNull, 1);
        } catch (NullPointerException e) {
            System.out.println("compare(null, 1) 抛出: " + e.getClass().getSimpleName());
        }

        printGuidance();
    }

    private static void printGuidance() {
        System.out.println("选型建议: 循环/算术用基本类型；泛型与集合用包装类；");
        System.out.println("          需要表达“没值”用包装类 null，例如数据库字段 age: Integer");
    }
}
