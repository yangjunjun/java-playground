package com.learn.collections;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Map 家族：HashMap / LinkedHashMap / TreeMap / Hashtable / ConcurrentHashMap。
 */
public class MapDemo {

    public static void demo() {
        // HashMap：允许一个 null 键与多个 null 值，不保证顺序
        Map<String, Integer> scores = new HashMap<>();
        scores.put("语文", 90);
        scores.put("数学", 95);
        scores.put("英语", 88);
        System.out.println("HashMap: " + scores);
        System.out.println("getOrDefault: " + scores.getOrDefault("物理", 0));
        System.out.println("computeIfAbsent: " + scores.computeIfAbsent("物理", k -> 60));

        // Map.Entry 遍历：entrySet 比 keySet + get 更高效
        for (Map.Entry<String, Integer> e : scores.entrySet()) {
            System.out.println("  Entry: " + e.getKey() + " => " + e.getValue());
        }

        // 默认初始容量 16、负载因子 0.75；元素数超过 capacity * loadFactor 就扩容为 2 倍
        Map<String, Integer> tuned = new HashMap<>(32, 0.75f);
        tuned.put("k", 1);
        System.out.println("可指定初始容量与负载因子: " + tuned);
        // Java 8+：链表长度 ≥ 8 且容量 ≥ 64 时树化为红黑树，查找由 O(n) 降为 O(log n)

        // LinkedHashMap：保持插入顺序；accessOrder=true 时按访问顺序（可做 LRU）
        Map<String, Integer> accessOrder = new LinkedHashMap<>(16, 0.75f, true);
        accessOrder.put("A", 1);
        accessOrder.put("B", 2);
        accessOrder.put("C", 3);
        accessOrder.get("A"); // 访问 A，A 移到末尾
        System.out.println("LinkedHashMap accessOrder=true: " + accessOrder);

        // TreeMap：按键排序（自然序或 Comparator），实现 NavigableMap
        Map<String, Integer> tree = new TreeMap<>(Comparator.reverseOrder());
        tree.put("banana", 2);
        tree.put("apple", 1);
        tree.put("cherry", 3);
        System.out.println("TreeMap 逆序: " + tree);

        NavigableMap<String, Integer> nav = new TreeMap<>(tree);
        System.out.println("floorEntry(banana): " + nav.floorEntry("banana"));
        System.out.println("headMap(cherry): " + nav.headMap("cherry"));

        // Hashtable：遗留同步 Map，不允许 null 键/值
        Map<String, Integer> legacy = new Hashtable<>();
        legacy.put("x", 1);
        System.out.println("Hashtable（遗留）: " + legacy);

        // ConcurrentHashMap：并发安全，不允许 null 键/值（Java 8+ CAS + synchronized）
        Map<String, Integer> concurrent = new ConcurrentHashMap<>();
        concurrent.put("y", 2);
        System.out.println("ConcurrentHashMap: " + concurrent);
    }
}
