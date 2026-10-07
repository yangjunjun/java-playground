package com.learn.str;

/**
 * 格式化输出：String.format / String.formatted / String.join。
 */
public class StringFormat {

    public static void demo() {
        String formatted = String.format("%s 年龄 %d 岁，成绩 %.2f", "张三", 18, 95.555);
        System.out.println("format: " + formatted);

        String instance = "左对齐[%-8s]|补零[%05.2f]|大写[%X]".formatted("hi", 3.14159, 255);
        System.out.println("formatted (Java 15+): " + instance);

        System.out.printf("printf 到控制台: 布尔=%b, 科学计数=%e%n", true, 12345.678);

        String joined = String.join(", ", "红", "绿", "蓝");
        System.out.println("join: " + joined);

        String joinedPath = String.join("/", "usr", "local", "bin");
        System.out.println("join 路径: " + joinedPath);
        System.out.println("join 空数组: [" + String.join(",", new String[0]) + "]");
    }
}
