package com.learn.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pattern / Matcher 基础：compile、matches、find、lookingAt。
 */
public class PatternMatcher {

    public static void demo() {
        // Pattern.compile 编译一次，可反复使用；Matcher 负责在输入串上执行匹配
        Pattern phone = Pattern.compile("\\d{3}-\\d{4}");

        // matches：整个输入串必须完全匹配
        Matcher m1 = phone.matcher("123-4567");
        System.out.println("matches(\"123-4567\") = " + m1.matches());

        Matcher m2 = phone.matcher("电话 123-4567 转 8");
        System.out.println("matches(\"电话 123-4567 转 8\") = " + m2.matches());

        // find：在输入串中查找下一个子串匹配
        Matcher m3 = phone.matcher("电话 123-4567 转 8");
        if (m3.find()) {
            System.out.println("find() 命中子串: " + m3.group() + "，位置 " + m3.start() + "-" + m3.end());
        }

        // lookingAt：从开头匹配，但不要求消耗整个输入串
        Matcher m4 = phone.matcher("123-4567 转 8");
        System.out.println("lookingAt(\"123-4567 转 8\") = " + m4.lookingAt());

        // String 便捷方法：内部每次都会重新编译正则
        System.out.println("\"abc123\".matches(\"[a-z]+[0-9]+\") = " + "abc123".matches("[a-z]+[0-9]+"));
    }
}
