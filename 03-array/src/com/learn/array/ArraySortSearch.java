package com.learn.array;

import java.util.Arrays;
import java.util.Comparator;

/**
 * 排序与查找：Arrays.sort / parallelSort / binarySearch。
 */
public class ArraySortSearch {

    static class Player implements Comparable<Player> {
        final String name;
        final int score;

        Player(String name, int score) {
            this.name = name;
            this.score = score;
        }

        @Override
        public int compareTo(Player other) {
            return Integer.compare(this.score, other.score);
        }

        @Override
        public String toString() {
            return name + "(" + score + ")";
        }
    }

    public static void demo() {
        int[] nums = { 5, 1, 4, 2, 3 };
        Arrays.sort(nums);
        System.out.println("sort 升序: " + Arrays.toString(nums));

        Integer[] boxed = { 5, 1, 4, 2, 3 };
        Arrays.sort(boxed, Comparator.reverseOrder());
        System.out.println("对象数组按 Comparator 降序: " + Arrays.toString(boxed));

        int[] big = new int[10];
        Arrays.setAll(big, i -> (i * 7) % 10);
        Arrays.parallelSort(big);
        System.out.println("parallelSort: " + Arrays.toString(big) + "（大数组并行更快）");

        System.out.println("binarySearch 找 2: " + Arrays.binarySearch(nums, 2));
        int missing = Arrays.binarySearch(nums, 9);
        // 找不到时返回 -(插入点) - 1：9 应插到下标 5，返回 -6
        System.out.println("binarySearch 找 9: " + missing + "（负数=找不到）");

        int[] unsorted = { 3, 1, 2 };
        System.out.println("未排序就 binarySearch(2): " + Arrays.binarySearch(unsorted, 2)
                + " ← 结果无意义，不能依赖");

        Player[] players = {
                new Player("甲", 70),
                new Player("乙", 90),
                new Player("丙", 80)
        };
        Arrays.sort(players);
        System.out.println("Comparable 自然序(按分数): " + Arrays.toString(players));
        Arrays.sort(players, Comparator.comparing(p -> p.name));
        System.out.println("Comparator 按名字: " + Arrays.toString(players));
    }
}
