package com.learn.modern;

/**
 * sealed 密封类/接口（Java 17+ 正式）。
 * 用 permits 精确控制哪些类可以继承，配合 switch 模式匹配可做穷尽性检查。
 */
public class SealedClasses {

    // 密封接口：只允许列出的三个实现
    sealed interface Shape permits Circle, Rectangle, Triangle {}

    record Circle(double r) implements Shape {}

    record Rectangle(double w, double h) implements Shape {}

    // final：再也不能被继承
    record Triangle(double a, double b, double c) implements Shape {}

    // 三种结束密封的方式：final / non-sealed / 继续 sealed
    sealed interface Animal permits Dog, Cat, Wild {}

    final record Dog(String name) implements Animal {}   // final：不能再被继承

    sealed interface Cat extends Animal permits HouseCat {}  // 继续密封

    record HouseCat(String name) implements Cat {}

    // non-sealed：重新放开，任何类都可再继承 Wild
    non-sealed interface Wild extends Animal {}

    // sealed + permits + record 实现：类型层次一眼看清
    sealed interface Result<T> permits Ok, Err {}

    record Ok<T>(T value) implements Result<T> {}

    record Err<T>(String message) implements Result<T> {}

    public static void demo() {
        Shape[] shapes = { new Circle(1.0), new Rectangle(2, 3), new Triangle(3, 4, 5) };
        for (Shape s : shapes) {
            System.out.println("密封子类: " + s.getClass().getSimpleName() + " -> " + s);
        }

        // switch 配合密封类型：全部列出后可省 default，编译器做穷尽性检查
        for (Shape s : shapes) {
            double area = switch (s) {
                case Circle c -> Math.PI * c.r() * c.r();
                case Rectangle r -> r.w() * r.h();
                case Triangle t -> {
                    double p = (t.a() + t.b() + t.c()) / 2;
                    yield Math.sqrt(p * (p - t.a()) * (p - t.b()) * (p - t.c()));
                }
            };
            System.out.println("  穷尽 switch 计算面积: " + area);
        }

        // 密封接口 + record 实现的 Result 模式
        Result<Integer> ok = new Ok<>(42);
        Result<Integer> err = new Err<>("除零");
        System.out.println("Result 模式: " + describe(ok) + " / " + describe(err));

        System.out.println("小结：sealed 用 permits 列出允许的子类，新增子类需改声明，层次受控");
    }

    private static String describe(Result<Integer> r) {
        return switch (r) {
            case Ok<Integer>(var v) -> "Ok(" + v + ")";
            case Err<Integer>(var msg) -> "Err(" + msg + ")";
        };
    }
}
