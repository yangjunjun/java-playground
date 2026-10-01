package com.learn.reflection;

/**
 * Class 对象：forName / .class / getClass() 三种获取方式与常用元信息。
 */
public class ClassBasics {

    /** 演示用的简单类 */
    public static class Animal {
        public void speak() {
        }
    }

    public static class Dog extends Animal implements Runnable {
        @Override
        public void run() {
        }
    }

    public static void demo() {
        // 三种获取 Class 的方式
        Class<?> c1 = Dog.class;                                  // 编译期确定
        Class<?> c2 = new Dog().getClass();                       // 运行时由实例得到
        Class<?> c3 = null;
        try {
            c3 = Class.forName("com.learn.reflection.ClassBasics$Dog"); // 全限定名（内部类用 $）
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
        System.out.println("三种方式是同一个 Class？ " + (c1 == c2 && c2 == c3));

        // 常用元信息
        System.out.println("全限定名: " + c1.getName());
        System.out.println("简单名: " + c1.getSimpleName());
        System.out.println("父类: " + c1.getSuperclass().getSimpleName());
        System.out.println("接口: " + java.util.Arrays.toString(c1.getInterfaces()));
        System.out.println("是否接口？ " + c1.isInterface() + ", 是否枚举？ " + c1.isEnum());

        // 类加载器
        System.out.println("类加载器: " + c1.getClassLoader());
    }
}
