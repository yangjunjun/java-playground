package com.learn.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 匹配标志：CASE_INSENSITIVE、MULTILINE、DOTALL 等。
 */
public class RegexFlags {

    private static void findAll(String title, Pattern p, String input) {
        Matcher m = p.matcher(input);
        System.out.println(title + " 对 \"" + input + "\":");
        boolean any = false;
        while (m.find()) {
            System.out.println("  命中 \"" + m.group() + "\"");
            any = true;
        }
        if (!any) {
            System.out.println("  (无匹配)");
        }
    }

    public static void demo() {
        // CASE_INSENSITIVE：忽略大小写
        findAll("默认", Pattern.compile("hello"), "Hello HELLO hello");
        findAll("CASE_INSENSITIVE", Pattern.compile("hello", Pattern.CASE_INSENSITIVE), "Hello HELLO hello");

        // 也可用内联标志 (?i)，作用范围从出现处到表达式结束
        findAll("内联 (?i)", Pattern.compile("(?i)hello"), "Hello HELLO");

        // MULTILINE：^ $ 匹配每一行的行首/行尾，而不只是整个输入的首尾
        String lines = "apple\nbanana\ncherry";
        findAll("默认 ^b", Pattern.compile("^b"), lines);
        findAll("MULTILINE ^b", Pattern.compile("^b", Pattern.MULTILINE), lines);

        // DOTALL：让 . 也匹配换行符
        findAll("默认 .", Pattern.compile("a.b"), "a\nb");
        findAll("DOTALL .", Pattern.compile("a.b", Pattern.DOTALL), "a\nb");

        // UNICODE_CASE（Java 全版本）+ 其他常用标志
        System.out.println("其他常用标志: UNICODE_CHARACTER_CLASS（\\w 匹配 Unicode）、LITERAL（整串当字面量）、UNIX_LINES");
    }
}
