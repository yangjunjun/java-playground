package com.learn.annotation;

/**
 * 注解学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. 内置注解");
        BuiltinAnnotations.demo();

        banner("2. 自定义注解");
        CustomAnnotation.demo();

        banner("3. 元注解");
        MetaAnnotation.demo();

        banner("4. 反射读取注解");
        AnnotationReflection.demo();

        banner("5. 重复注解");
        RepeatableAnnotation.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
