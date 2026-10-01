package com.learn.exception;

/**
 * try-with-resources（Java 7）：自动关闭 AutoCloseable 资源 + 被抑制异常。
 */
public class TryWithResources {

    /** 一个可关闭资源；close 时可能抛异常 */
    static class MyResource implements AutoCloseable {
        private final String name;
        private final boolean failOnClose;

        MyResource(String name, boolean failOnClose) {
            this.name = name;
            this.failOnClose = failOnClose;
        }

        void work() {
            System.out.println(name + " 工作中");
        }

        @Override
        public void close() {
            System.out.println(name + " 关闭");
            if (failOnClose) {
                throw new RuntimeException(name + " 关闭失败");
            }
        }
    }

    public static void demo() {
        System.out.println("-- 基本用法（Java 7）：按声明逆序自动关闭 --");
        try (MyResource r1 = new MyResource("资源A", false);
             MyResource r2 = new MyResource("资源B", false)) {
            r1.work();
            r2.work();
        } catch (Exception e) {
            System.out.println("不应到达: " + e);
        }

        System.out.println("-- 引用已存在的资源（Java 9+，资源变量须是 final 或等效 final）--");
        MyResource r = new MyResource("资源C", false);
        try (r) {
            r.work();
        }

        System.out.println("-- 被抑制异常（suppressed）：close 的异常不会吞掉主异常 --");
        try (MyResource bad = new MyResource("资源D", true)) {
            bad.work();
            throw new IllegalStateException("业务异常"); // 主异常
        } catch (Exception e) {
            System.out.println("主异常: " + e.getMessage());
            Throwable[] suppressed = e.getSuppressed();
            for (Throwable s : suppressed) {
                System.out.println("被抑制异常: " + s.getMessage());
            }
        }
    }
}
