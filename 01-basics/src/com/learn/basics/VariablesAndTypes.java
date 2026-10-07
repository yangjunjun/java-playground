package com.learn.basics;

/**
 * 变量与基本类型：8 种基本类型、字面量写法、默认值、加宽/窄化转换、var 推断。
 */
public class VariablesAndTypes {

    /** 字段有默认值，局部变量没有 */
    static class Defaults {
        byte b;
        short s;
        int i;
        long l;
        float f;
        double d;
        char c;
        boolean flag;
        Object obj;

        void print() {
            System.out.println("字段默认值: byte=" + b + ", short=" + s + ", int=" + i
                    + ", long=" + l + ", float=" + f + ", double=" + d);
            System.out.println("char 默认是空字符，码点=" + (int) c
                    + "；boolean=" + flag + "；引用类型=" + obj);
        }
    }

    public static void demo() {
        byte b = 100;
        short s = 30000;
        int i = 100000;
        long l = 3_000_000_000L;
        float f = 3.14F;
        double d = 3.1415926;
        char c = 'A';
        boolean flag = true;

        System.out.println("byte=" + b + ", short=" + s + ", int=" + i + ", long=" + l);
        System.out.println("float=" + f + ", double=" + d + ", char=" + c + ", boolean=" + flag);

        int binary = 0b1010;
        int octal = 017;
        int hex = 0xFF;
        int million = 1_000_000;
        char unicodeChar = '\u4E2D';
        System.out.println("二进制 0b1010=" + binary + "，八进制 017=" + octal
                + "，十六进制 0xFF=" + hex + "，1_000_000=" + million
                + "，\\u4E2D=" + unicodeChar);

        new Defaults().print();

        widening();
        narrowing();

        var msg = "类型被推断为 String";
        var count = 42;
        System.out.println("var: " + msg + "，count=" + count);
        System.out.println("（switch 表达式、record、模式匹配等见 modern-syntax 模块）");
    }

    private static void widening() {
        byte b = 10;
        int i = b;
        long l = i;
        float f = l;
        double d = f;
        System.out.println("加宽 byte→int→long→float→double: " + d);
    }

    private static void narrowing() {
        double d = 3.99;
        int i = (int) d;
        System.out.println("3.99 强转 int = " + i + "（截断小数，不是四舍五入）");

        int big = 130;
        byte b = (byte) big;
        // 130 的二进制低 8 位是 0x82，按补码解释为 -126
        System.out.println("130 强转 byte = " + b + "（溢出按补码回绕）");

        char c = (char) 65;
        System.out.println("65 强转 char = " + c);
    }
}
