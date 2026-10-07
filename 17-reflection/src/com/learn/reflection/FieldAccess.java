package com.learn.reflection;

import java.lang.reflect.Field;

/**
 * 字段反射：getDeclaredField、setAccessible、读写私有字段。
 * Java 9+ 模块系统下跨模块反射 JDK 内部会抛 InaccessibleObjectException。
 */
public class FieldAccess {

    public static class Person {
        private String name = "原始名";
        public int age = 18;
        private static String species = "人类";
    }

    public static void demo() {
        try {
            Person p = new Person();

            // public 字段：getField 直接拿
            Field age = Person.class.getField("age");
            System.out.println("public 字段 age = " + age.get(p));

            // private 字段：getDeclaredField + setAccessible(true)
            Field name = Person.class.getDeclaredField("name");
            name.setAccessible(true);
            System.out.println("private 字段 name = " + name.get(p));
            name.set(p, "新名字");   // 反射写入
            System.out.println("修改后 name = " + name.get(p));

            // 静态字段：invoke/get 传 null
            Field species = Person.class.getDeclaredField("species");
            species.setAccessible(true);
            System.out.println("静态字段 species = " + species.get(null));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
