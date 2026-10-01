package com.learn.collections;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedMap;
import java.util.SequencedSet;

/**
 * SequencedCollection / SequencedSet / SequencedMap（Java 21+）。
 * 统一了 List、Deque、LinkedHashSet、LinkedHashMap 等“有两端顺序”集合的 API。
 */
public class SequencedCollections {

    public static void demo() {
        // List 实现 SequencedCollection：新增 getFirst / getLast / reversed / addFirst / addLast
        List<String> list = new ArrayList<>(List.of("a", "b", "c"));
        System.out.println("getFirst: " + list.getFirst());
        System.out.println("getLast: " + list.getLast());
        System.out.println("reversed: " + list.reversed());

        list.addFirst("start");
        list.addLast("end");
        System.out.println("addFirst / addLast 后: " + list);

        // LinkedHashSet 实现 SequencedSet
        SequencedSet<String> set = new LinkedHashSet<>(List.of("x", "y", "z"));
        System.out.println("SequencedSet first / last: " + set.getFirst() + " / " + set.getLast());
        System.out.println("SequencedSet reversed: " + set.reversed());

        // LinkedHashMap 实现 SequencedMap
        SequencedMap<String, Integer> map = new LinkedHashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        System.out.println("SequencedMap firstEntry: " + map.firstEntry());
        System.out.println("SequencedMap lastEntry: " + map.lastEntry());
        System.out.println("SequencedMap reversed: " + map.reversed());
    }
}
