package com.learn.collections;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * List：ArrayList / LinkedList 对比，遗留的 Vector，遍历与 fail-fast 机制。
 */
public class ListDemo {

    public static void demo() {
        // ArrayList：基于数组，随机访问 O(1)，尾部追加均摊 O(1)，中间插入要搬移元素
        List<String> arrayList = new ArrayList<>();
        arrayList.add("Java");
        arrayList.add("Go");
        arrayList.add("Python");
        arrayList.add("Rust");
        arrayList.add(1, "C"); // 中间插入
        System.out.println("ArrayList: " + arrayList);
        System.out.println("get(2) = " + arrayList.get(2) + "（随机访问很快）");

        // LinkedList：基于双向链表，头尾操作 O(1)，随机访问 O(n)
        LinkedList<String> linked = new LinkedList<>(arrayList);
        linked.addFirst("头");
        linked.addLast("尾");
        System.out.println("LinkedList 头尾操作: " + linked);

        // Vector：遗留类，方法带 synchronized，性能差，单线程别用
        Vector<Integer> vector = new Vector<>();
        vector.add(1);
        vector.add(2);
        System.out.println("Vector（遗留，同步版 ArrayList）: " + vector);

        // --- 遍历方式 ---
        System.out.println("-- for-each 遍历 --");
        for (String s : arrayList) {
            System.out.println("  " + s);
        }
        System.out.println("-- 迭代器遍历 --");
        for (Iterator<String> it = arrayList.iterator(); it.hasNext(); ) {
            System.out.println("  " + it.next());
        }

        // --- fail-fast：遍历中结构性修改会抛 ConcurrentModificationException ---
        List<String> forFailFast = new ArrayList<>(List.of("a", "b", "c", "d"));
        try {
            for (String s : forFailFast) {
                if ("b".equals(s)) {
                    forFailFast.remove(s); // 错误示范：for-each 中直接删除
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("捕获 fail-fast 异常: " + e.getClass().getSimpleName());
        }

        // 正确做法一：用迭代器自己的 remove
        for (Iterator<String> it = forFailFast.iterator(); it.hasNext(); ) {
            if ("c".equals(it.next())) {
                it.remove();
            }
        }
        System.out.println("迭代器 remove 后: " + forFailFast);

        // 正确做法二：Java 8+ 的 removeIf
        forFailFast.removeIf(s -> s.equals("d"));
        System.out.println("removeIf 后: " + forFailFast);
    }
}
