package com.learn.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则语法：字符类 \d \w \s、量词（贪婪/懒惰/独占）、锚点 ^ $ \b。
 */
public class RegexSyntax {

    private static void show(String title, String regex, String input) {
        Matcher m = Pattern.compile(regex).matcher(input);
        System.out.println(title + "  /" + regex + "/  对 \"" + input + "\":");
        if (!m.find()) {
            System.out.println("  (无匹配)");
            return;
        }
        do {
            System.out.println("  命中 \"" + m.group() + "\" @ " + m.start());
        } while (m.find());
    }

    public static void demo() {
        // ---- 字符类 ----
        show("数字", "\\d+", "订单号 A1002 与 B33");
        show("单词字符", "\\w+", "你好 Java_25!");
        show("空白", "\\s+", "a  b\tc");
        show("自定义类", "[a-f0-9]{2}", "颜色 #c0ffee 与 #zz");
        show("取反类", "[^0-9]+", "ab12cd34ef");

        // ---- 量词 ----
        // 贪婪：尽量多匹配
        show("贪婪 .*", "<.+>", "<b>粗体</b>");
        // 懒惰（reluctant）：尽量少匹配
        show("懒惰 .*?", "<.+?>", "<b>粗体</b>");
        // 独占（possessive）：不回溯，宁可整体失败（Java 独有写法 +）
        show("独占 (失败示例)", "<.++>", "<b>粗体</b>");
        show("计数 {n,m}", "\\d{2,4}", "1 12 123 1234 12345");

        // ---- 锚点 ----
        show("行首 ^", "^\\w+", "hello world");
        show("整串 ^...$", "^\\d+$", "12345");
        show("单词边界 \\b", "\\bcat\\b", "cat catalog concat");
    }
}
