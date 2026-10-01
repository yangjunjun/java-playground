package com.learn.i18n;

import java.util.Locale;

/**
 * Locale：语言 / 国家 / 变体，以及默认 Locale 与切换。
 */
public class LocaleDemo {

    public static void demo() {
        // ---- Locale 的三要素：language（小写）_ COUNTRY（大写）_ variant ----
        Locale zhCn = Locale.of("zh", "CN");           // 简体中文-中国
        Locale enUs = Locale.of("en", "US");           // 英语-美国
        Locale zhTw = Locale.of("zh", "TW");           // 中文-台湾（繁体）
        System.out.println("zh_CN: " + zhCn + " → " + zhCn.getDisplayName());
        System.out.println("en_US: " + enUs + " → " + enUs.getDisplayName());
        System.out.println("zh_TW: " + zhTw + " → " + zhTw.getDisplayName());

        // 显示名也可以本地化：用对方的语言介绍自己
        System.out.println("用法语介绍 zh_CN: " + zhCn.getDisplayName(Locale.FRENCH));

        // ---- 常量 Locale ----
        System.out.println("常量: Locale.CHINA / Locale.US / Locale.JAPAN / Locale.ROOT");
        System.out.println("Locale.CHINA = " + Locale.CHINA
                + ", Locale.US = " + Locale.US
                + ", Locale.ROOT = '" + Locale.ROOT + "'（语言无关的回退）");

        // ---- 默认 Locale ----
        Locale def = Locale.getDefault();
        System.out.println("当前默认 Locale: " + def
                + "（JVM 取操作系统/用户设置，可用 -Duser.language=en -Duser.country=US 覆盖）");

        // 切换默认 Locale（影响 NumberFormat / DateFormat 等不带 Locale 参数的工厂方法）
        try {
            Locale.setDefault(Locale.US);
            System.out.println("切换默认 Locale 后: " + Locale.getDefault());
            Locale.setDefault(Locale.CHINA);
            System.out.println("再切回 zh_CN: " + Locale.getDefault());
        } finally {
            Locale.setDefault(def); // 演示结束恢复
        }

        // ---- 带脚本的 Locale（Java 7+ 扩展，现代 BCP 47）----
        Locale simplified = Locale.forLanguageTag("zh-Hans-CN"); // 简体
        Locale traditional = Locale.forLanguageTag("zh-Hant-TW"); // 繁体
        System.out.println("BCP 47 标签: " + simplified.toLanguageTag()
                + " / " + traditional.toLanguageTag());
        System.out.println("建议：业务代码始终显式传 Locale，不要依赖默认值");
    }
}
