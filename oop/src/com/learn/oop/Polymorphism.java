package com.learn.oop;

/**
 * 多态：向上转型、动态分派、instanceof 模式匹配（Java 16+）。
 */
public class Polymorphism {

    static class Shape {
        double area() {
            return 0;
        }

        String kind() {
            return "图形";
        }
    }

    static class Circle extends Shape {
        double r;

        Circle(double r) {
            this.r = r;
        }

        @Override
        double area() {
            return Math.PI * r * r;
        }

        @Override
        String kind() {
            return "圆形";
        }
    }

    static class Rect extends Shape {
        double w, h;

        Rect(double w, double h) {
            this.w = w;
            this.h = h;
        }

        @Override
        double area() {
            return w * h;
        }

        @Override
        String kind() {
            return "矩形";
        }
    }

    /** 统一处理父类型引用，实际执行哪个方法看对象真实类型（动态分派） */
    static void printArea(Shape s) {
        System.out.println(s.kind() + " 面积 = " + s.area());
    }

    public static void demo() {
        System.out.println("-- 向上转型 + 动态分派 --");
        Shape a = new Circle(2);  // 向上转型：子类对象赋给父类引用
        Shape b = new Rect(3, 4);
        printArea(a);
        printArea(b);

        System.out.println("-- instanceof 模式匹配（Java 16+）--");
        // 旧写法：if (a instanceof Circle) { Circle c = (Circle) a; ... }
        if (a instanceof Circle c) {
            System.out.println("a 是圆形，半径 = " + c.r);
        }
        if (b instanceof Rect r) {
            System.out.println("b 是矩形，宽 = " + r.w + ", 高 = " + r.h);
        }

        System.out.println("-- switch 模式匹配（Java 21+）--");
        for (Shape s : new Shape[] { a, b }) {
            String desc = switch (s) {
                case Circle c -> "圆形 r=" + c.r;
                case Rect r -> "矩形 " + r.w + "x" + r.h;
                default -> "未知图形";
            };
            System.out.println(desc);
        }
    }
}
