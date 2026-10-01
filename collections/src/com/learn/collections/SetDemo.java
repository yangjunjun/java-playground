package com.learn.collections;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Set：HashSet / LinkedHashSet / TreeSet 与 equals/hashCode 契约。
 */
public class SetDemo {

    /** 正确实现 equals/hashCode 的值对象 */
    static class Point {
        final int x;
        final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Point)) {
                return false;
            }
            Point p = (Point) o;
            return x == p.x && y == p.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }

    /** 违反契约：只重写 equals，不重写 hashCode */
    static class BrokenPoint {
        final int x;
        final int y;

        BrokenPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof BrokenPoint)) {
                return false;
            }
            BrokenPoint p = (BrokenPoint) o;
            return x == p.x && y == p.y;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }

    public static void demo() {
        // HashSet：无序，基于 HashMap，允许一个 null
        Set<String> hash = new HashSet<>(List.of("banana", "apple", "cherry", "apple"));
        System.out.println("HashSet（无序、去重）: " + hash);

        // LinkedHashSet：保持插入顺序
        Set<String> linked = new LinkedHashSet<>(List.of("banana", "apple", "cherry"));
        System.out.println("LinkedHashSet（插入顺序）: " + linked);

        // TreeSet：按自然序或 Comparator 排序，基于红黑树
        Set<String> tree = new TreeSet<>(List.of("banana", "apple", "cherry"));
        System.out.println("TreeSet（排序）: " + tree);

        // equals/hashCode 契约：equals 相等则 hashCode 必须相等
        Set<Point> points = new HashSet<>();
        points.add(new Point(1, 2));
        System.out.println("正确契约，contains 生效: " + points.contains(new Point(1, 2)));

        // 只重写 equals：hashCode 不同，落到不同桶，contains 失效
        Set<BrokenPoint> broken = new HashSet<>();
        broken.add(new BrokenPoint(1, 2));
        System.out.println("违反契约，contains 失效: " + broken.contains(new BrokenPoint(1, 2)));
    }
}
