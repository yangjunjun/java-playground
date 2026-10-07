package com.learn.str;

/**
 * String 基础：常量池、== 与 equals、常用方法。
 */
public class StringBasics {

    public static void demo() {
        String a = "hello";
        String b = "hello";
        String c = new String("hello");
        System.out.println("字面量 a==b: " + (a == b) + "（常量池同一对象）");
        System.out.println("a==new String: " + (a == c) + "（堆上新对象）");
        System.out.println("a.equals(c): " + a.equals(c));

        String interned = c.intern();
        System.out.println("c.intern() 后 a==interned: " + (a == interned));

        String s = "Hello, Java!";
        System.out.println("length=" + s.length() + ", isEmpty=" + s.isEmpty());
        System.out.println("charAt(0)=" + s.charAt(0) + ", indexOf(\"Java\")=" + s.indexOf("Java")
                + ", indexOf(\"xyz\")=" + s.indexOf("xyz") + "（找不到返回 -1）");
        System.out.println("substring(7, 11)=" + s.substring(7, 11) + "（含头不含尾）");
        System.out.println("replace(Java, World)=" + s.replace("Java", "World"));

        String csv = "红,绿,蓝";
        String[] parts = csv.split(",");
        System.out.print("split: ");
        for (String p : parts) {
            System.out.print(p + "/");
        }
        System.out.println();

        System.out.println("contains(Java)=" + s.contains("Java")
                + ", startsWith(Hello)=" + s.startsWith("Hello")
                + ", endsWith(!)=" + s.endsWith("!"));
        System.out.println("toUpperCase=" + s.toUpperCase() + ", toLowerCase=" + s.toLowerCase());
        System.out.println("equalsIgnoreCase: " + "Hello".equalsIgnoreCase("hello"));
    }
}
