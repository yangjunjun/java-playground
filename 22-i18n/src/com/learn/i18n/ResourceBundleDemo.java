package com.learn.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * ResourceBundle：按 Locale 加载属性资源束。
 * <p>
 * 资源文件放在 {@code src/i18n/Messages*.properties}，
 * baseName 为 {@code i18n.Messages}（点分隔），run.sh 把 {@code src} 加进 classpath。
 */
public class ResourceBundleDemo {

    private static final String BASE = "i18n.Messages";

    public static void demo() {
        // ---- 按 Locale 加载（文件在 src/i18n/ 目录）----
        ResourceBundle zh = ResourceBundle.getBundle(BASE, Locale.of("zh", "CN"));
        ResourceBundle en = ResourceBundle.getBundle(BASE, Locale.US);
        ResourceBundle fallback = ResourceBundle.getBundle(BASE, Locale.ROOT);

        System.out.println("zh_CN 标题: " + zh.getString("app.title"));
        System.out.println("en_US 标题: " + en.getString("app.title"));
        System.out.println("ROOT 回退:  " + fallback.getString("app.title"));

        // ---- 带占位符的键（配合 MessageFormat 使用）----
        System.out.println("zh_CN greeting 模板: " + zh.getString("greeting"));
        System.out.println("en_US greeting 模板: " + en.getString("greeting"));

        // ---- 回退链：zh_CN → zh → 兜底 Messages.properties ----
        // 没有 Messages_zh_TW 时，会一路回退到默认束，不会抛异常
        ResourceBundle tw = ResourceBundle.getBundle(BASE, Locale.of("zh", "TW"));
        System.out.println("zh_TW 回退到: " + tw.getString("app.title")
                + "（没有 Messages_zh_TW，走回退链）");

        // ---- 缺失键 ----
        try {
            zh.getString("no.such.key");
        } catch (MissingResourceException e) {
            System.out.println("缺失键会抛 MissingResourceException（可用 containsKey 预先判断）");
        }
        System.out.println("containsKey(\"greeting\"): " + zh.containsKey("greeting"));

        // ---- 遍历所有键 ----
        System.out.println("zh_CN 全部键:");
        zh.keySet().forEach(k -> System.out.println("  " + k + " = " + zh.getString(k)));

        // 命名规则：BaseName_language_country_variant.properties
        //   Messages_zh_CN.properties  → 中文-中国
        //   Messages_zh.properties     → 所有中文（更宽回退）
        //   Messages.properties       → 兜底
        System.out.println("命名规则: BaseName_language_COUNTRY.properties，越具体越优先");
    }
}
