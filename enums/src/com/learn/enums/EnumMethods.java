package com.learn.enums;

/**
 * 枚举常用方法与比较：compareTo、equals、天然可序列化/可比较。
 */
public class EnumMethods {

    enum Level {
        LOW, MEDIUM, HIGH, URGENT
    }

    public static void demo() {
        System.out.println("-- compareTo：按声明（ordinal）顺序比较 --");
        System.out.println("HIGH.compareTo(LOW) = " + Level.HIGH.compareTo(Level.LOW));
        System.out.println("LOW.compareTo(HIGH) = " + Level.LOW.compareTo(Level.HIGH));

        System.out.println("-- equals / == 等价（枚举常量是单例）--");
        Level a = Level.HIGH;
        Level b = Level.valueOf("HIGH");
        System.out.println("a == b ? " + (a == b) + ", a.equals(b) ? " + a.equals(b));

        System.out.println("-- 天然实现 Comparable 与 Serializable --");
        System.out.println("Comparable.isAssignableFrom(Level) ? "
                + Comparable.class.isAssignableFrom(Level.class));
        System.out.println("java.io.Serializable.isAssignableFrom(Level) ? "
                + java.io.Serializable.class.isAssignableFrom(Level.class));

        System.out.println("-- 天然线程安全的单例：序列化不会破坏唯一性 --");
        System.out.println("每个常量都是 JVM 内单例，反序列化后仍是同一实例");
    }
}
