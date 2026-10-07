package com.learn.i18n;

import java.text.ChoiceFormat;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * MessageFormat：占位符、数字/日期格式化嵌入，以及 ChoiceFormat 复数分支。
 */
public class MessageFormatDemo {

    public static void demo() {
        ResourceBundle zh = ResourceBundle.getBundle("i18n.Messages", Locale.CHINA);
        ResourceBundle en = ResourceBundle.getBundle("i18n.Messages", Locale.US);

        // ---- 基本占位符 {0} {1} ----
        System.out.println("zh: " + MessageFormat.format(zh.getString("greeting"), "小明"));
        System.out.println("en: " + MessageFormat.format(en.getString("greeting"), "Alice"));

        // ---- 带类型的占位符：{0,number} {2,date} ----
        // 注意：子格式的 Locale 在构造时就固化了，setLocale 之后必须重新 applyPattern
        String orderPattern = "订单 {0}：{1,number,currency}，时间 {2,date,short}";
        MessageFormat cnFmt = new MessageFormat(orderPattern, Locale.CHINA);
        System.out.println("复合格式(中国): "
                + cnFmt.format(new Object[]{ 1001, 199.5, new java.util.Date(0) }));

        MessageFormat usFmt = new MessageFormat(orderPattern, Locale.US);
        System.out.println("复合格式(美国): "
                + usFmt.format(new Object[]{ 1001, 199.5, new java.util.Date(0) }));

        // ---- ChoiceFormat：按数值区间选分支（复数/量词本地化）----
        // 模式: 数值|匹配文本 用 # 分隔
        String pattern = "{0,choice,0#没有文件|1#只有一个文件|1<共有 {0,number,integer} 个文件}";
        MessageFormat cf = new MessageFormat(pattern, Locale.US);
        System.out.println("ChoiceFormat 0: " + cf.format(new Object[]{ 0 }));
        System.out.println("ChoiceFormat 1: " + cf.format(new Object[]{ 1 }));
        System.out.println("ChoiceFormat 5: " + cf.format(new Object[]{ 5 }));

        // ---- 常见坑：单引号 ----
        // MessageFormat 里单引号是转义符！想输出字面 ' 要写两个
        System.out.println("单引号转义: " + MessageFormat.format("It''s {0}", "ok"));
        System.out.println("注意：资源文件里含单引号时也要按 MessageFormat 规则转义");
        System.out.println("建议：模板放资源束，参数在代码里传，永远不要把用户输入拼进模板");
    }
}
