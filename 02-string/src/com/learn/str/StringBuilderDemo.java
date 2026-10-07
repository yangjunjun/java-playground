package com.learn.str;

/**
 * StringBuilder / StringBuffer：可变字符序列，链式修改。
 */
public class StringBuilderDemo {

    public static void demo() {
        StringBuilder sb = new StringBuilder("hello");
        sb.append(" world");
        System.out.println("append: " + sb);

        sb.insert(0, ">> ");
        System.out.println("insert(0): " + sb);

        sb.replace(0, 2, "«");
        System.out.println("replace(0,2): " + sb);

        sb.deleteCharAt(0);
        System.out.println("deleteCharAt(0): " + sb);

        sb.reverse();
        System.out.println("reverse: " + sb);
        sb.reverse();

        System.out.println("length=" + sb.length() + ", capacity=" + sb.capacity());
        sb.append("12345678901234567890");
        System.out.println("追加超出容量后自动扩容: capacity=" + sb.capacity());

        String result = sb.toString();
        System.out.println("toString: " + result);

        compareBufferBuilder();
    }

    private static void compareBufferBuilder() {
        StringBuffer buffer = new StringBuffer("a");
        StringBuilder builder = new StringBuilder("a");
        buffer.append("b");
        builder.append("b");
        // 二者 API 基本一致；StringBuffer 方法带 synchronized，线程安全但更慢
        System.out.println("StringBuffer: " + buffer + "（方法同步，线程安全）");
        System.out.println("StringBuilder: " + builder + "（无同步，单线程更快）");
        System.out.println("选择建议: 无共享场景一律 StringBuilder");
    }
}
