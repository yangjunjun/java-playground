package com.learn.modern;

/**
 * record 记录类（Java 16+ 正式）。
 * 不可变数据载体：自动生成 equals/hashCode/toString/访问器，适合 DTO、值对象。
 */
public class Records {

    // record 定义：一行声明字段，自动获得构造器与访问器
    record Point(int x, int y) {
        // 紧凑构造器（compact constructor）：只写校验/归一化，字段赋值隐式完成
        Point {
            if (x < 0 || y < 0) {
                throw new IllegalArgumentException("坐标不能为负: " + x + "," + y);
            }
        }

        // 静态工厂方法很常用
        static Point origin() {
            return new Point(0, 0);
        }

        // 可以实现接口
        String describe() {
            return "Point(" + x + ", " + y + ")";
        }
    }

    interface Shape {
        double area();
    }

    record Circle(double r) implements Shape {
        @Override
        public double area() {
            return Math.PI * r * r;
        }
    }

    // 改造前：手写不可变类，一堆模板代码
    static final class OldPoint {
        private final int x;
        private final int y;

        OldPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof OldPoint p && p.x == x && p.y == y;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(x) * 31 + y;
        }

        @Override
        public String toString() {
            return "OldPoint[x=" + x + ", y=" + y + "]";
        }
    }

    public static void demo() {
        // 改造前
        OldPoint old = new OldPoint(1, 2);
        System.out.println("旧写法手写模板: " + old + ", equals=" + old.equals(new OldPoint(1, 2)));

        // 改造后
        var p = new Point(3, 4);
        System.out.println("record 自动生成: " + p);
        // 访问器是 x() / y()，不是 getX() / getY()
        System.out.println("访问器命名: x()=" + p.x() + ", y()=" + p.y());
        System.out.println("静态工厂: " + Point.origin());
        System.out.println("紧凑构造器校验: ");
        try {
            new Point(-1, 0);
        } catch (IllegalArgumentException e) {
            System.out.println("  捕获 -> " + e.getMessage());
        }

        // equals/hashCode/toString 都自动生成，且是值语义
        var q = new Point(3, 4);
        System.out.println("值语义 equals: " + p.equals(q) + ", hashCode 相等=" + (p.hashCode() == q.hashCode()));

        // 实现接口
        Shape c = new Circle(2.0);
        System.out.println("record 实现接口: 面积=" + c.area());

        System.out.println("小结：record 是不可变数据载体，不是可变 JavaBean 的替代品（没有 setter）");
    }
}
