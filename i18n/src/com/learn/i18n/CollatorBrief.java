package com.learn.i18n;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Collator：按语言习惯排序/比较字符串（拼音、笔画、字典序）。
 */
public class CollatorBrief {

    public static void demo() {
        List<String> cities = new ArrayList<>(
                List.of("上海", "北京", "广州", "深圳", "重庆", "天津"));

        // ---- 默认字符串排序：按 UTF-16 码点，对中文毫无意义 ----
        List<String> byCodePoint = new ArrayList<>(cities);
        Collections.sort(byCodePoint);
        System.out.println("String.compareTo 排序: " + byCodePoint);
        System.out.println("（这只是码点顺序，不是拼音也不是笔画）");

        // ---- Collator：按 Locale 的排序规则 ----
        Collator zh = Collator.getInstance(Locale.CHINA);
        List<String> byPinyin = new ArrayList<>(cities);
        byPinyin.sort(zh);
        System.out.println("Collator(zh_CN) 排序: " + byPinyin);

        Collator en = Collator.getInstance(Locale.US);
        List<String> words = new ArrayList<>(List.of("apple", "Banana", "apple pie", "applepie"));
        words.sort(en);
        System.out.println("Collator(en_US) 排序: " + words);

        // ---- 比较 ----
        System.out.println("比较 \"上海\" vs \"北京\": " + zh.compare("上海", "北京"));
        System.out.println("Collator 会忽略大小写/重音差异（取决于 strength 设置）");

        // ---- 强度设置 ----
        // PRIMARY：只看主要字母；SECONDARY：看重音；TERTIARY：看大小写；IDENTICAL：完全一致
        Collator primary = Collator.getInstance(Locale.US);
        primary.setStrength(Collator.PRIMARY);
        System.out.println("PRIMARY 强度下 \"a\" == \"A\": " + (primary.compare("a", "A") == 0));

        Collator tertiary = Collator.getInstance(Locale.US);
        tertiary.setStrength(Collator.TERTIARY);
        System.out.println("TERTIARY 强度下 \"a\" == \"A\": " + (tertiary.compare("a", "A") == 0));

        System.out.println("要点：做本地化排序/搜索必须用 Collator，不要用 String.compareTo");
    }
}
