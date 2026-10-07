package com.learn.annotation;

/**
 * 内置注解：@Override / @Deprecated / @SuppressWarnings / @FunctionalInterface
 */
public class BuiltinAnnotations {

    static class Animal {
        public void speak() {
            System.out.println("动物发出声音");
        }

        @Deprecated
        public void oldMove() {
            System.out.println("旧的移动方式（已过时）");
        }
    }

    static class Dog extends Animal {
        // @Override 让编译器校验：这里必须真的是重写父类方法
        @Override
        public void speak() {
            System.out.println("汪汪汪");
        }
    }

    // @FunctionalInterface 保证接口只有一个抽象方法，可安全用于 Lambda
    @FunctionalInterface
    interface Greeting {
        String greet(String name);
    }

    @SuppressWarnings("unused")
    private static int unusedField = 42;

    public static void demo() {
        Dog dog = new Dog();
        dog.speak();

        // 调用过时方法会有编译警告；@SuppressWarnings("deprecation") 可压制
        @SuppressWarnings("deprecation")
        Animal animal = dog;
        animal.oldMove();

        Greeting g = name -> "你好, " + name;
        System.out.println(g.greet("Java"));
    }
}
