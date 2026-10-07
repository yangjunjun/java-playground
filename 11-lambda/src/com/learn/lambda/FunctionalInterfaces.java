package com.learn.lambda;

/**
 * 函数式接口与 @FunctionalInterface；重载歧义示例。
 */
public class FunctionalInterfaces {

    // 只有一个抽象方法（SAM）才是函数式接口；default / static 方法不影响
    @FunctionalInterface
    interface Calculator {
        int calc(int a, int b);

        default int inc(int x) {
            return calc(x, 1);
        }

        static int zero() {
            return 0;
        }
    }

    // 有两个抽象方法就不是函数式接口，去掉注释会编译失败：
    // @FunctionalInterface
    // interface NotFunctional {
    //     void a();
    //     void b();
    // }

    interface Foo {
        void run(String s);
    }

    interface Bar {
        void run(Integer i);
    }

    static void overloaded(Foo f) {
        f.run("Foo");
    }

    static void overloaded(Bar b) {
        b.run(1);
    }

    public static void demo() {
        Calculator plus = (a, b) -> a + b;
        System.out.println("calc: " + plus.calc(20, 22) + "，inc: " + plus.inc(41));
        System.out.println("static 方法: " + Calculator.zero());

        // 方法引用也能作为函数式接口实例
        Calculator max = Math::max;
        System.out.println("Math::max: " + max.calc(3, 9));

        // 重载歧义：两个重载方法参数都是函数式接口时，lambda 无法推断目标类型
        // overloaded(s -> System.out.println(s)); // 编译错误：目标类型不明确
        overloaded((Foo) s -> System.out.println("显式转型消除歧义: " + s));
        overloaded((Bar) i -> System.out.println("显式转型消除歧义: " + i));
    }
}
