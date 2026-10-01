package com.learn.collections;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Queue / Deque：PriorityQueue、ArrayDeque，以及栈的用法。
 */
public class QueueDeque {

    public static void demo() {
        // PriorityQueue：基于堆，出队顺序按优先级（自然序或 Comparator）
        Queue<Integer> pq = new PriorityQueue<>(List.of(5, 1, 3, 2));
        System.out.print("PriorityQueue 出队顺序: ");
        while (!pq.isEmpty()) {
            System.out.print(pq.poll() + " ");
        }
        System.out.println();

        // 用 Comparator 控制优先级
        Queue<String> tasks = new PriorityQueue<>(Comparator.comparingInt(String::length));
        tasks.addAll(List.of("java", "go", "kotlin"));
        System.out.println("按长度优先: " + tasks.poll() + " -> " + tasks.poll());

        // ArrayDeque：双端队列，比 LinkedList 更快，可当栈或队列用
        Deque<String> deque = new ArrayDeque<>();
        deque.addLast("A");  // 队尾入队（队列用法）
        deque.addLast("B");
        deque.addFirst("Z"); // 队头入队（双端特性）
        System.out.println("ArrayDeque: " + deque);
        System.out.println("pollFirst（队列出口）: " + deque.pollFirst());
        System.out.println("pollLast（栈出口）: " + deque.pollLast());

        // 栈用法：push / pop / peek
        Deque<String> stack = new ArrayDeque<>();
        stack.push("一");
        stack.push("二");
        System.out.println("栈 peek: " + stack.peek() + "，pop: " + stack.pop());
    }
}
