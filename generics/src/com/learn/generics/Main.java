package com.learn.generics;

/**
 * 泛型学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. 泛型类与泛型接口");
        GenericClass.demo();

        banner("2. 泛型方法与类型推断");
        GenericMethod.demo();

        banner("3. 有界类型参数");
        BoundedTypes.demo();

        banner("4. 通配符与 PECS");
        Wildcards.demo();

        banner("5. 类型擦除");
        TypeErasure.demo();

        banner("6. 泛型陷阱与最佳实践");
        GenericPitfalls.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
