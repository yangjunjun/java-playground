package com.learn.enums;

/**
 * 枚举基础：enum 是特殊的类，name/ordinal、values/valueOf。
 */
public class EnumBasics {

    /** 每个枚举常量都是该类的单例实例；隐式 public static final */
    enum Season {
        SPRING, SUMMER, AUTUMN, WINTER
    }

    public static void demo() {
        System.out.println("-- name() 与 ordinal() --");
        Season s = Season.SUMMER;
        System.out.println("s = " + s);
        System.out.println("s.name() = " + s.name() + ", s.ordinal() = " + s.ordinal());

        System.out.println("-- values()：按声明顺序返回数组 --");
        for (Season item : Season.values()) {
            System.out.println("  " + item.name() + " (序号 " + item.ordinal() + ")");
        }

        System.out.println("-- valueOf(String)：按名称解析，找不到抛 IllegalArgumentException --");
        Season parsed = Season.valueOf("WINTER");
        System.out.println("valueOf(\"WINTER\") = " + parsed);
        try {
            Season.valueOf("winter"); // 区分大小写！
        } catch (IllegalArgumentException e) {
            System.out.println("valueOf(\"winter\") 失败: " + e.getMessage());
        }

        System.out.println("-- 枚举本质：继承 java.lang.Enum 的 final 类 --");
        System.out.println("Season.getSuperclass() = " + Season.class.getSuperclass().getSimpleName());
        System.out.println("Season 是 final ? " + java.lang.reflect.Modifier.isFinal(Season.class.getModifiers()));
    }
}
