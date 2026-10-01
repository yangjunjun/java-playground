package com.learn.regex;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Java 11+ 便捷 API：asMatchPredicate / asPredicate / results()，以及常用配方。
 */
public class RegexJava11Plus {

    public static void demo() {
        List<String> words = List.of("cat", "catalog", "concat", "Cat", "dog");

        // asMatchPredicate（Java 11+）：整串完全匹配，等价于 matcher(s).matches()
        List<String> exact = words.stream()
                .filter(Pattern.compile("cat").asMatchPredicate())
                .collect(Collectors.toList());
        System.out.println("asMatchPredicate 完整匹配 \"cat\": " + exact);

        // asPredicate（Java 11+）：子串包含匹配，等价于 matcher(s).find()
        List<String> contains = words.stream()
                .filter(Pattern.compile("cat").asPredicate())
                .collect(Collectors.toList());
        System.out.println("asPredicate 包含匹配 \"cat\": " + contains);

        // results()（Java 9+）：把所有匹配以 Stream 形式取出
        String text = "2024-07-01 与 2025-01-15";
        List<String> dates = Pattern.compile("\\d{4}-\\d{2}-\\d{2}")
                .matcher(text)
                .results()
                .map(r -> r.group())
                .collect(Collectors.toList());
        System.out.println("results() 提取日期: " + dates);

        // ---- 常用配方（务必了解其局限）----
        // “类邮箱”校验：能挡明显错误，但不是 RFC 5322 完整实现
        String emailish = "^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        System.out.println("邮箱式校验 \"a.b+c@sub.example.com\": "
                + Pattern.matches(emailish, "a.b+c@sub.example.com"));
        System.out.println("邮箱式校验 \"不是邮箱\": " + Pattern.matches(emailish, "不是邮箱"));

        // 整数 / 小数
        System.out.println("整数 \"-42\": " + Pattern.matches("[-+]?\\d+", "-42"));
        System.out.println("小数 \"3.14\": " + Pattern.matches("[-+]?\\d+(\\.\\d+)?", "3.14"));

        // 简易日期 YYYY-MM-DD（不含真实日历校验，2 月 31 日也能通过）
        String dateish = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$";
        System.out.println("日期式 \"2024-02-29\": " + Pattern.matches(dateish, "2024-02-29"));
        System.out.println("日期式 \"2024-02-31\"（假阳性，需日历校验）: " + Pattern.matches(dateish, "2024-02-31"));
    }
}
