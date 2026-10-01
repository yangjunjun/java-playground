package com.learn.generics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 泛型类与泛型接口：类型参数化容器 + 实用 Repository 示例。
 */
public class GenericClass {

    /** 泛型类：T 是类型参数，使用时再确定 */
    static class Box<T> {
        private T content;

        void put(T item) {
            this.content = item;
        }

        T get() {
            return content;
        }
    }

    /** 多个类型参数 */
    static class Pair<K, V> {
        final K key;
        final V value;

        Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    /** 泛型接口 */
    interface Repository<T> {
        void save(T entity);

        T findById(int id);
    }

    /** 实用示例：内存版用户仓库，编译期保证只存 User */
    static class User {
        final int id;
        final String name;

        User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "User(" + id + ", " + name + ")";
        }
    }

    static class InMemoryUserRepository implements Repository<User> {
        private final Map<Integer, User> table = new HashMap<>();

        @Override
        public void save(User entity) {
            table.put(entity.id, entity);
        }

        @Override
        public User findById(int id) {
            return table.get(id);
        }
    }

    public static void demo() {
        System.out.println("-- Box<T> --");
        Box<String> box = new Box<>();
        box.put("Hello 泛型");
        String s = box.get(); // 无需强转，编译期已确定类型
        System.out.println("box.get() = " + s);

        System.out.println("-- Pair<K, V> --");
        Pair<String, Integer> pair = new Pair<>("年龄", 18);
        System.out.println(pair);

        System.out.println("-- Repository<User> 实用示例 --");
        Repository<User> repo = new InMemoryUserRepository();
        repo.save(new User(1, "小明"));
        repo.save(new User(2, "小红"));
        User u = repo.findById(1);
        System.out.println("findById(1) = " + u);

        System.out.println("-- 标准库同样泛型化 --");
        List<String> list = new ArrayList<>();
        list.add("A");
        list.add("B");
        System.out.println("List<String> = " + list);
    }
}
