package com.learn.oop;

/**
 * 内部类：成员内部类、静态嵌套类、局部内部类、匿名内部类。
 */
public class InnerClasses {

    /** 外部类 */
    static class Outer {
        private String msg = "外部类字段";

        /** 成员内部类：持有外部类引用，可直接访问外部私有成员 */
        class Inner {
            void show() {
                System.out.println("成员内部类访问: " + msg);
            }
        }

        /** 静态嵌套类：不依附外部实例，不能直接访问外部实例成员 */
        static class Nested {
            void show() {
                System.out.println("静态嵌套类（无外部实例引用）");
            }
        }

        void localDemo() {
            /** 局部内部类：定义在方法内，作用域仅限该方法 */
            class Local {
                void show() {
                    System.out.println("局部内部类访问: " + msg);
                }
            }
            new Local().show();
        }

        Runnable anonymousDemo() {
            // 匿名内部类：没有类名，常用于一次性实现接口/抽象类
            return new Runnable() {
                @Override
                public void run() {
                    System.out.println("匿名内部类实现 Runnable");
                }
            };
        }
    }

    public static void demo() {
        Outer outer = new Outer();

        System.out.println("-- 成员内部类 --");
        Outer.Inner inner = outer.new Inner(); // 语法：外部实例.new 内部类()
        inner.show();

        System.out.println("-- 静态嵌套类 --");
        new Outer.Nested().show();

        System.out.println("-- 局部内部类 --");
        outer.localDemo();

        System.out.println("-- 匿名内部类 --");
        outer.anonymousDemo().run();
    }
}
