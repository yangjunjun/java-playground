package com.learn.i18n;

/**
 * 国际化（i18n）学习总入口：依次运行各知识点示例。
 * 资源束位于 src/i18n/*.properties，run.sh 已把 src 加入 classpath。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. Locale 基础");
        LocaleDemo.demo();

        banner("2. ResourceBundle 资源束");
        ResourceBundleDemo.demo();

        banner("3. MessageFormat 占位符");
        MessageFormatDemo.demo();

        banner("4. NumberFormat 数字格式");
        NumberFormatDemo.demo();

        banner("5. 日期时间本地化");
        DateFormatI18n.demo();

        banner("6. Collator 排序");
        CollatorBrief.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
