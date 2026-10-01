package com.learn.wrapper;

/**
 * 解析与转换：parseInt / valueOf / decode 与反向字符串化。
 */
public class ParseUtils {

    public static void demo() {
        int n = Integer.parseInt("42");
        int binary = Integer.parseInt("1010", 2);
        int octal = Integer.parseInt("17", 8);
        int hex = Integer.parseInt("1F", 16);
        System.out.println("parseInt: \"42\"→" + n + ", 二进制 1010→" + binary
                + ", 八进制 17→" + octal + ", 十六进制 1F→" + hex);

        Integer boxed = Integer.valueOf("42");
        System.out.println("valueOf 返回 Integer（-128~127 命中缓存）: " + boxed);

        System.out.println("decode 支持前缀: 0x1F→" + Integer.decode("0x1F")
                + ", #1F→" + Integer.decode("#1F")
                + ", 017→" + Integer.decode("017")
                + ", -0x10→" + Integer.decode("-0x10"));

        System.out.println("其他类型: Double.parseDouble(\"3.14\")→" + Double.parseDouble("3.14")
                + ", Boolean.parseBoolean(\"true\")→" + Boolean.parseBoolean("true")
                + ", Long.parseLong(\"9223372036854775807\")→" + Long.parseLong("9223372036854775807"));

        try {
            Integer.parseInt("12x");
        } catch (NumberFormatException e) {
            System.out.println("非法数字抛出: " + e.getClass().getSimpleName()
                    + "，消息=" + e.getMessage());
        }

        System.out.println("反向转换: toString(255)→" + Integer.toString(255)
                + ", toHexString(255)→" + Integer.toHexString(255)
                + ", toBinaryString(10)→" + Integer.toBinaryString(10)
                + ", toOctalString(8)→" + Integer.toOctalString(8));
    }
}
