package com.learn.exception;

import java.util.List;

/**
 * 异常处理最佳实践 + 常见异常类型速览。
 */
public class ExceptionBestPractices {

    /** 常见运行时异常 */
    static void commonExceptions() {
        System.out.println("NullPointerException: 空引用解引用"
                + "（Java 14+ 增强消息可指出具体变量，如 \"Cannot read field \\\"x\\\" because \\\"p\\\" is null\"）");

        List<String> list = List.of("a", "b");
        try {
            int n = Integer.parseInt("abc");
            System.out.println("不应到达: " + n);
        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException: 数字格式错误 → " + e.getMessage());
        }

        Object obj = "hello";
        try {
            Integer n = (Integer) obj;
            System.out.println("不应到达: " + n);
        } catch (ClassCastException e) {
            System.out.println("ClassCastException: 强转类型不匹配");
        }

        try {
            System.out.println(list.get(5));
        } catch (IndexOutOfBoundsException e) {
            System.out.println("IndexOutOfBoundsException: 索引越界");
        }
    }

    /** 反例1：吞异常 —— 问题被隐藏，线上极难排查 */
    static void swallowBad() {
        try {
            Integer.parseInt("x");
        } catch (Exception e) {
            // 什么都不做 = 吞异常。至少记日志，或包装后抛出
        }
        System.out.println("反例：吞异常后假装成功（不要学）");
    }

    /** 正例：捕获具体类型，只处理能处理的，其余上抛 */
    static int parseOr(String s, int fallback) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("解析失败，使用默认值: " + s);
            return fallback;
        }
    }

    public static void demo() {
        System.out.println("-- 常见异常速览 --");
        commonExceptions();

        System.out.println("-- 反例：吞异常 --");
        swallowBad();

        System.out.println("-- 正例：具体异常 + 有意义的兜底 --");
        System.out.println("parseOr(\"42\", 0) = " + parseOr("42", 0));
        System.out.println("parseOr(\"x\", -1) = " + parseOr("x", -1));

        System.out.println("-- 原则 --");
        System.out.println("1. 捕获具体异常，避免 catch (Exception e) 一刀切");
        System.out.println("2. 不吞异常：要么处理，要么包装上抛，至少记日志");
        System.out.println("3. 用异常表达错误，不要用返回码; finally 里不要 return");
        System.out.println("4. 优先 try-with-resources 管理资源，而不是手工 close");
    }
}
