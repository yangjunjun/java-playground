package com.learn.oop;

/**
 * 面向对象学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. 封装：类、构造器与访问权限");
        Encapsulation.demo();

        banner("2. 继承：extends / 重写 / super / final");
        Inheritance.demo();

        banner("3. 多态：向上转型与动态分派");
        Polymorphism.demo();

        banner("4. 抽象类与接口");
        AbstractInterface.demo();

        banner("5. 内部类");
        InnerClasses.demo();

        banner("6. Object 常用方法");
        ObjectMethods.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
