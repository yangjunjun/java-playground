package com.learn.exception;

/**
 * 自定义异常：受检/非受检两种写法，serialVersionUID 说明。
 */
public class CustomException {

    /** 自定义受检异常：继承 Exception，调用方必须处理 */
    static class InvalidAgeException extends Exception {
        // 序列化兼容用；异常可能跨进程传输，显式声明避免编译器自动生成的值不一致
        private static final long serialVersionUID = 1L;

        private final int age;

        InvalidAgeException(int age) {
            super("非法年龄: " + age);
            this.age = age;
        }

        int getAge() {
            return age;
        }
    }

    /** 自定义非受检异常：继承 RuntimeException，适合编程错误/业务断言 */
    static class InsufficientBalanceException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        InsufficientBalanceException(double need, double actual) {
            super("余额不足: 需要 " + need + ", 实际 " + actual);
        }
    }

    static void register(String name, int age) throws InvalidAgeException {
        if (age < 0 || age > 150) {
            throw new InvalidAgeException(age);
        }
        System.out.println("注册成功: " + name + ", " + age + " 岁");
    }

    static void withdraw(double need, double actual) {
        if (actual < need) {
            throw new InsufficientBalanceException(need, actual);
        }
        System.out.println("取款 " + need + " 成功");
    }

    public static void demo() {
        System.out.println("-- 自定义受检异常 --");
        try {
            register("小明", 200);
        } catch (InvalidAgeException e) {
            System.out.println("捕获: " + e.getMessage() + "（age = " + e.getAge() + "）");
        }

        System.out.println("-- 自定义非受检异常 --");
        try {
            withdraw(100, 30);
        } catch (InsufficientBalanceException e) {
            System.out.println("捕获: " + e.getMessage());
        }
    }
}
