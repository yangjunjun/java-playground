package com.learn.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Collections 工具类：排序、查找、同步包装、不可修改视图、频次统计。
 */
public class CollectionsUtil {

    public static void demo() {
        List<Integer> numbers = new ArrayList<>(List.of(3, 1, 4, 1, 5, 9, 2, 6));

        Collections.sort(numbers); // 自然序排序
        System.out.println("Collections.sort: " + numbers);
        numbers.sort(Comparator.reverseOrder()); // List.sort（Java 8+）
        System.out.println("List.sort 逆序: " + numbers);

        Collections.sort(numbers); // binarySearch 前必须先有序
        System.out.println("binarySearch(5): " + Collections.binarySearch(numbers, 5));
        System.out.println("frequency(1): " + Collections.frequency(numbers, 1));
        System.out.println("max / min: " + Collections.max(numbers) + " / " + Collections.min(numbers));

        // 同步包装：单线程别用；要线程安全请用 java.util.concurrent 包
        List<Integer> synced = Collections.synchronizedList(new ArrayList<>(numbers));
        System.out.println("synchronizedList: " + synced);

        // 不可修改视图：是“视图”，底层集合变了视图跟着变
        List<Integer> src = new ArrayList<>(List.of(1, 2, 3));
        List<Integer> view = Collections.unmodifiableList(src);
        src.add(4);
        System.out.println("unmodifiable 视图随底层变化: " + view);
        try {
            view.add(5);
        } catch (UnsupportedOperationException e) {
            System.out.println("视图拒绝修改: " + e.getClass().getSimpleName());
        }

        // 其他常用
        List<Integer> shuffled = new ArrayList<>(List.of(1, 2, 3, 4));
        Collections.shuffle(shuffled);
        System.out.println("shuffle: " + shuffled);
    }
}
