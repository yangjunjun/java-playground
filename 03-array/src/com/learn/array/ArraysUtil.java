package com.learn.array;

import java.util.Arrays;
import java.util.List;

/**
 * java.util.Arrays 工具类：toString/fill/copyOf/equals/setAll/compare/mismatch 与 List.of 对比。
 */
public class ArraysUtil {

    public static void demo() {
        int[] arr = { 3, 1, 2 };
        System.out.println("toString: " + Arrays.toString(arr));
        System.out.println("deepToString: " + Arrays.deepToString(new int[][] { { 1, 2 }, { 3 } }));

        int[] filled = new int[3];
        Arrays.fill(filled, 7);
        System.out.println("fill 全部=7: " + Arrays.toString(filled));
        Arrays.fill(filled, 1, 3, 8);
        System.out.println("fill[1,3)=8: " + Arrays.toString(filled));

        int[] copied = Arrays.copyOf(arr, 5);
        System.out.println("copyOf 扩容: " + Arrays.toString(copied) + "（新增位补 0）");

        int[] same = { 3, 1, 2 };
        System.out.println("equals 内容比较: " + Arrays.equals(arr, same)
                + ", == 引用比较: " + (arr == same));

        int[] generated = new int[4];
        Arrays.setAll(generated, i -> i * 10);
        System.out.println("setAll(i -> i*10): " + Arrays.toString(generated));

        int[] x = { 1, 2, 3 };
        int[] y = { 1, 2, 4 };
        System.out.println("compare(x,y)=" + Arrays.compare(x, y) + "（负数表示 x 字典序更小）");
        System.out.println("mismatch(x,y)=" + Arrays.mismatch(x, y) + "（第一个不同元素的下标）");

        listVsArray();
    }

    private static void listVsArray() {
        List<String> list = List.of("a", "b");
        String[] arr = { "a", "b" };
        System.out.println("List.of 不可变: " + list);
        try {
            // List.of 的元素、长度都不可更改
            list.set(0, "z");
        } catch (UnsupportedOperationException e) {
            System.out.println("list.set 抛出: " + e.getClass().getSimpleName());
        }

        arr[0] = "z";
        System.out.println("数组元素可改: " + Arrays.toString(arr)
                + "；数组还能存基本类型 int[]，List 只能装包装类");
    }
}
