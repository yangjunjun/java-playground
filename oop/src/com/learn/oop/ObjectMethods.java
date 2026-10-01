package com.learn.oop;

/**
 * Object 常用方法：toString / equals / hashCode / clone，以及避免 finalize。
 */
public class ObjectMethods {

    static class Point implements Cloneable {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /** 重写 toString：打印对象时输出可读内容，而不是类名@哈希 */
        @Override
        public String toString() {
            return "Point(" + x + ", " + y + ")";
        }

        /** 重写 equals 必须同时重写 hashCode：相等对象必须有相同哈希 */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Point p)) { // instanceof 模式（Java 16+）
                return false;
            }
            return x == p.x && y == p.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }

        /** clone 默认是浅拷贝；需实现 Cloneable 并把 protected 放宽为 public */
        @Override
        public Point clone() {
            try {
                return (Point) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e); // 实现了 Cloneable，理论上不会发生
            }
        }
    }

    public static void demo() {
        System.out.println("-- toString --");
        Point p1 = new Point(1, 2);
        Point p2 = new Point(1, 2);
        System.out.println("p1 = " + p1);

        System.out.println("-- equals / hashCode --");
        System.out.println("p1.equals(p2) = " + p1.equals(p2));
        System.out.println("p1.hashCode() = " + p1.hashCode() + ", p2.hashCode() = " + p2.hashCode());

        System.out.println("-- clone 浅拷贝 --");
        Point p3 = p1.clone();
        System.out.println("p3 = " + p3 + ", p3 == p1 ? " + (p3 == p1));

        System.out.println("-- finalize 已被弃用（Java 18+ @Deprecated(forRemoval)）--");
        System.out.println("请勿依赖 finalize 做资源释放，改用 try-with-resources 或 Cleaner（Java 9+）");
    }
}
