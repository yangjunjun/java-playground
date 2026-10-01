package com.learn.collections;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 并发集合（简介）：CopyOnWriteArrayList / ConcurrentHashMap / 阻塞队列。
 * 完整并发内容请见 concurrency 模块。
 */
public class ConcurrentCollectionsBrief {

    public static void demo() {
        // CopyOnWriteArrayList：写时复制，读多写少；迭代器遍历旧快照，不会抛 CME
        List<String> cow = new CopyOnWriteArrayList<>(List.of("a", "b", "c"));
        for (String s : cow) {
            if ("b".equals(s)) {
                cow.add("b-new"); // 遍历中修改也不会 ConcurrentModificationException
            }
            System.out.println("  遍历到: " + s);
        }
        System.out.println("CopyOnWriteArrayList 结果: " + cow);

        // ConcurrentHashMap：并发安全，不允许 null 键/值（Java 8+ CAS + synchronized）
        Map<String, Integer> chm = new ConcurrentHashMap<>();
        chm.put("count", 1);
        chm.merge("count", 1, Integer::sum);
        System.out.println("ConcurrentHashMap merge: " + chm);

        // 阻塞队列：生产者-消费者模式常用
        Queue<Integer> queue = new LinkedBlockingQueue<>(2);
        queue.offer(1);
        queue.offer(2);
        System.out.println("LinkedBlockingQueue: " + queue);
        System.out.println("更多并发容器（ConcurrentSkipListMap 等）详见 concurrency 模块。");
    }
}
