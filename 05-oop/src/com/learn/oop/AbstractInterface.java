package com.learn.oop;

/**
 * 抽象类与接口：区别、default/static/private 接口方法（Java 8/9）。
 */
public class AbstractInterface {

    /** 抽象类：可有字段、构造器、具体方法，单继承 */
    abstract static class AbstractLogger {
        protected String name;

        AbstractLogger(String name) {
            this.name = name;
        }

        abstract void log(String msg);

        void prefix() {
            System.out.print("[" + name + "] ");
        }
    }

    static class ConsoleLogger extends AbstractLogger {
        ConsoleLogger() {
            super("控制台");
        }

        @Override
        void log(String msg) {
            prefix();
            System.out.println(msg);
        }
    }

    /**
     * 接口：常量 + 抽象方法 + default（Java 8）+ static（Java 8）+ private（Java 9）。
     * 类可实现多个接口，弥补单继承限制。
     */
    interface Greeter {
        String greet(String name); // 抽象方法，实现类必须实现

        default String greetPolite(String name) {
            return wrap(greet(name)); // default 方法可调用 private 辅助方法（Java 9+）
        }

        static String shout(String s) {
            return s + "!!!";
        }

        private String wrap(String s) {
            return "【" + s + "】";
        }
    }

    interface ChineseGreeter {
        default String greetPolite(String name) {
            return "您好，" + name;
        }
    }

    /** 同时实现多个接口时，重名 default 方法必须显式重写 */
    static class HelloService implements Greeter, ChineseGreeter {
        @Override
        public String greet(String name) {
            return "你好 " + name;
        }

        @Override
        public String greetPolite(String name) {
            // 可用 接口名.super.method() 挑选某个接口的 default 实现
            return ChineseGreeter.super.greetPolite(name) + "（中文版）";
        }
    }

    public static void demo() {
        System.out.println("-- 抽象类 --");
        AbstractLogger logger = new ConsoleLogger();
        logger.log("抽象类方法被调用");

        System.out.println("-- 接口 default / static / private 方法 --");
        Greeter g = new HelloService();
        System.out.println(g.greet("Java"));
        System.out.println(g.greetPolite("Java"));
        System.out.println(Greeter.shout("静态接口方法"));

        System.out.println("-- 小结：抽象类适合 is-a 族谱 + 共享状态；接口适合能力契约 --");
    }
}
