package com.learn.reflection;

/**
 * 反射学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. Class 对象与类信息");
        ClassBasics.demo();

        banner("2. 反射创建对象");
        Instantiation.demo();

        banner("3. 字段访问");
        FieldAccess.demo();

        banner("4. 方法调用");
        MethodInvoke.demo();

        banner("5. 构造器反射");
        ConstructorAccess.demo();

        banner("6. 运行时注解");
        AnnotationReflect.demo();

        banner("7. 动态代理");
        ProxyReflect.demo();

        banner("8. 常见异常与注意点");
        ReflectPitfalls.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
