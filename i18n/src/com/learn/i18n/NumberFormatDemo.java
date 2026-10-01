package com.learn.i18n;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * NumberFormat：货币 / 百分比 / 整数 / 小数，按 Locale 呈现。
 */
public class NumberFormatDemo {

    public static void demo() {
        double money = 1234567.891;
        double ratio = 0.8567;
        long big = 1234567890L;

        Locale[] locales = { Locale.CHINA, Locale.US, Locale.JAPAN, Locale.GERMANY };

        // ---- 货币 ----
        System.out.println("货币格式（1234567.891）:");
        for (Locale loc : locales) {
            NumberFormat nf = NumberFormat.getCurrencyInstance(loc);
            System.out.println("  " + loc + " → " + nf.format(money));
        }

        // ---- 百分比 ----
        System.out.println("百分比格式（0.8567）:");
        for (Locale loc : locales) {
            NumberFormat nf = NumberFormat.getPercentInstance(loc);
            System.out.println("  " + loc + " → " + nf.format(ratio));
        }

        // ---- 整数（含分组分隔符）----
        System.out.println("整数格式（1234567890）:");
        for (Locale loc : locales) {
            NumberFormat nf = NumberFormat.getIntegerInstance(loc);
            System.out.println("  " + loc + " → " + nf.format(big));
        }

        // ---- 控制小数位 ----
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        System.out.println("固定两位小数: " + nf.format(money));

        // ---- 解析（逆操作）----
        try {
            Number parsed = NumberFormat.getNumberInstance(Locale.US).parse("1,234,567.89");
            System.out.println("解析 \"1,234,567.89\" = " + parsed);
        } catch (Exception e) {
            System.out.println("解析失败: " + e);
        }

        // ---- 货币的本地化注意 ----
        System.out.println("注意：getCurrencyInstance 用默认货币；跨币种请用 Currency + setCurrency");
        System.out.println("金额计算请用 BigDecimal，不要用 double 做钱");
    }
}
