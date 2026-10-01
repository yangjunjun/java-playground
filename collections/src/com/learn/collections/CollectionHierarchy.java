package com.learn.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.ArrayDeque;

/**
 * 集合体系结构：Collection 与 Map 两大根接口及其主要子接口/实现。
 */
public class CollectionHierarchy {

    public static void demo() {
        // Collection 是单列集合的根接口：List / Set / Queue 都继承它
        Collection<String> list = new ArrayList<>(List.of("a", "b", "c"));
        Collection<String> set = new HashSet<>(List.of("a", "b", "a"));
        Queue<String> queue = new ArrayDeque<>(List.of("x", "y"));

        System.out.println("List 允许重复、有序: " + list);
        System.out.println("Set 不允许重复: " + set);
        System.out.println("Queue 先进先出: " + queue);

        // Map 是双列集合（键值对）的根接口，不继承 Collection
        Map<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        System.out.println("Map 键值对: " + map);

        // 常见实现类速查
        System.out.println("List 实现: ArrayList / LinkedList / Vector(遗留)");
        System.out.println("Set 实现: HashSet / LinkedHashSet / TreeSet");
        System.out.println("Map 实现: HashMap / LinkedHashMap / TreeMap / Hashtable(遗留)");
        System.out.println("Queue 实现: PriorityQueue / ArrayDeque / LinkedList");
    }
}
