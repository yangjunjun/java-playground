package com.learn.modern;

/**
 * 模式匹配：instanceof 模式（Java 16+）、switch 模式匹配（Java 21+）、
 * record 解构模式（Java 21+）、守卫子句 when（Java 21+）。
 */
public class PatternMatching {

    record Point(int x, int y) {}

    record Line(Point start, Point end) {}

    sealed interface Shape permits Circle, Square {}

    record Circle(Point center, double r) implements Shape {}

    record Square(Point topLeft, double side) implements Shape {}

    // ---- 改造前：instanceof 后手动强转 ----
    static String oldDescribe(Object obj) {
        if (obj instanceof String) {
            String s = (String) obj; // 手动强转
            return "字符串长度=" + s.length();
        }
        return "未知";
    }

    // ---- 改造后：instanceof 模式变量（Java 16+）----
    static String newDescribe(Object obj) {
        if (obj instanceof String s) { // 一步完成判断 + 绑定
            return "字符串长度=" + s.length();
        }
        return "未知";
    }

    public static void demo() {
        System.out.println("instanceof 改造前: " + oldDescribe("hello"));
        System.out.println("instanceof 改造后: " + newDescribe("hello"));

        // switch 模式匹配（Java 21+）：直接在 case 里绑定类型
        Object[] objs = { "abc", 42, 3.14, new Point(1, 2) };
        for (Object o : objs) {
            String desc = switch (o) {
                case String s -> "字符串: " + s + " (长度" + s.length() + ")";
                case Integer i -> "整数: " + i;
                case Double d -> "浮点: " + d;
                case Point p -> "点: (" + p.x() + ", " + p.y() + ")";
                case null -> "null 值";
                default -> "其他: " + o.getClass().getSimpleName();
            };
            System.out.println("switch 模式: " + desc);
        }

        // 守卫子句 when（Java 21+）：在 case 内再加条件
        for (Object o : objs) {
            String desc = switch (o) {
                case String s when s.length() > 2 -> "长字符串: " + s;
                case String s -> "短字符串: " + s;
                case Integer i when i > 100 -> "大整数";
                case Integer i -> "小整数: " + i;
                case null, default -> "其他";
            };
            System.out.println("守卫 when: " + desc);
        }

        // record 解构模式（Java 21+）：直接拆出 record 分量
        Point p = new Point(3, 4);
        if (p instanceof Point(int x, int y)) {
            System.out.println("record 解构: x=" + x + ", y=" + y);
        }

        // 嵌套解构：Line 里套 Point
        Line line = new Line(new Point(0, 0), new Point(5, 5));
        if (line instanceof Line(Point(int x1, int y1), Point(int x2, int y2))) {
            System.out.println("嵌套解构: (" + x1 + "," + y1 + ")->(" + x2 + "," + y2 + ")");
        }

        // 密封 + 解构 + 守卫：求面积
        Shape[] shapes = { new Circle(new Point(0, 0), 2), new Square(new Point(1, 1), 3) };
        for (Shape s : shapes) {
            double area = switch (s) {
                case Circle(Point(int x, int y), double r) when r > 0 -> Math.PI * r * r;
                case Circle(_, _) -> 0;
                case Square(Point(int x, int y), double side) -> side * side;
            };
            System.out.println("解构求面积: " + area);
        }

        System.out.println("小结：模式匹配把“判断 + 解包 + 强转”合并成一步，when 负责加条件");
    }
}
