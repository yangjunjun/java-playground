package com.learn.reflection;

import java.lang.reflect.Method;

/**
 * 方法反射：getDeclaredMethod + 参数类型、invoke、静态方法、解包 InvocationTargetException。
 */
public class MethodInvoke {

    public static class Greeter {
        public String greet(String name) {
            return "你好, " + name;
        }

        private int add(int a, int b) {
            return a + b;
        }

        public static String staticHello() {
            return "静态方法返回";
        }

        public void boom() {
            throw new IllegalArgumentException("业务炸了");
        }
    }

    public static void demo() {
        try {
            Greeter g = new Greeter();

            // public 实例方法
            Method greet = Greeter.class.getDeclaredMethod("greet", String.class);
            System.out.println("invoke greet: " + greet.invoke(g, "Java"));

            // private 方法：setAccessible 后调用
            Method add = Greeter.class.getDeclaredMethod("add", int.class, int.class);
            add.setAccessible(true);
            System.out.println("invoke add(2,3): " + add.invoke(g, 2, 3));

            // 静态方法：目标对象传 null
            Method sh = Greeter.class.getDeclaredMethod("staticHello");
            System.out.println("invoke static: " + sh.invoke(null));

            // 目标方法抛异常 -> InvocationTargetException，用 getCause() 解包
            try {
                Greeter.class.getDeclaredMethod("boom").invoke(g);
            } catch (java.lang.reflect.InvocationTargetException e) {
                System.out.println("解包异常: " + e.getCause());
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
