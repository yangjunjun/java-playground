package com.learn.oop;

/**
 * 继承：extends、方法重写、super、final、super() 构造器链。
 */
public class Inheritance {

    static class Animal {
        String name;

        Animal(String name) {
            this.name = name;
            System.out.println("Animal 构造器: " + name);
        }

        void speak() {
            System.out.println(name + " 发出声音");
        }
    }

    static class Dog extends Animal {
        String breed;

        Dog(String name, String breed) {
            super(name); // 必须先调用父类构造器，且位于子类构造器第一行
            this.breed = breed;
            System.out.println("Dog 构造器: " + breed);
        }

        @Override
        void speak() {
            System.out.println(name + "（" + breed + "）汪汪叫");
        }

        void fetch() {
            super.speak(); // super 调用被重写前的父类版本
            System.out.println(name + " 去捡球");
        }
    }

    /** final 类不能被继承（如 String、Integer） */
    final static class IdCard {
        void show() {
            System.out.println("身份证号展示（final 类，不可继承）");
        }
    }

    static class BaseService {
        /** final 方法不能被子类重写 */
        final void start() {
            System.out.println("服务启动（final 方法，不可重写）");
        }

        void run() {
            System.out.println("基础服务运行中");
        }
    }

    static class ChildService extends BaseService {
        @Override
        void run() {
            System.out.println("子服务运行中（重写了 run）");
        }
    }

    public static void demo() {
        System.out.println("-- 构造器链：先父后子 --");
        Dog dog = new Dog("旺财", "柯基");
        dog.speak();

        System.out.println("-- super 调用父类实现 --");
        dog.fetch();

        System.out.println("-- final 方法/类 --");
        ChildService svc = new ChildService();
        svc.start();
        svc.run();
        new IdCard().show();
    }
}
