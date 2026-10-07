package com.learn.modern;

/**
 * 匿名模式与匿名变量（Java 21 预览 / Java 22 正式）。
 * 用下划线 _ 表示“这个分量我不关心”，让模式匹配更清晰。
 */
public class UnnamedPatterns {

    record Point(int x, int y) {}

    record Pair(Point left, Point right) {}

    public static void demo() {
        // 匿名模式：只关心第一个分量
        Object o = new Point(7, 99);
        if (o instanceof Point(int x, int _)) { // y 被忽略
            System.out.println("匿名模式，只取 x: " + x);
        }

        // 匿名模式：整条匹配但不绑定
        if (o instanceof Point _) {
            System.out.println("整条 Point 匹配成功（未绑定变量）");
        }

        // switch 中忽略不关心的分量
        Object[] objs = { "hello", 42, new Point(1, 2) };
        for (Object obj : objs) {
            String desc = switch (obj) {
                case String _ -> "是字符串（不关心内容）";
                case Integer _ -> "是整数（不关心大小）";
                case Point(int x, int _) -> "是点，x=" + x;
                case null, default -> "其他";
            };
            System.out.println("switch 匿名模式: " + desc);
        }

        // 嵌套解构时忽略整段
        Pair pair = new Pair(new Point(1, 2), new Point(3, 4));
        if (pair instanceof Pair(Point(int x, int _), _)) { // 右边整个 Point 不关心
            System.out.println("嵌套解构忽略右点: x=" + x);
        }

        // 匿名局部变量（Java 22+）：声明后不使用
        try {
            int result = 10 / 0;
            System.out.println(result);
        } catch (ArithmeticException _) { // 不需要异常变量
            System.out.println("捕获异常但不绑定变量（匿名变量 _）");
        }

        // 多个忽略的分量
        Object p2 = new Point(5, 6);
        if (p2 instanceof Point(int _, int y)) { // x 被忽略
            System.out.println("忽略 x 只取 y: " + y);
        }

        System.out.println("小结：_ 表示“有意忽略”，比写未使用变量更诚实、更清晰");
    }
}
