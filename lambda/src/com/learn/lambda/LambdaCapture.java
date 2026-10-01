package com.learn.lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntUnaryOperator;

/**
 * 变量捕获：effectively final 约束与 this 语义。
 */
public class LambdaCapture {

    private String field = "实例字段";

    public static void demo() {
        LambdaCapture demo = new LambdaCapture();
        demo.run();
    }

    private void run() {
        // 局部变量必须是 effectively final 才能被 lambda 捕获
        int base = 10;
        // base = 20; // 去掉注释会编译失败：base 不再是 effectively final
        IntUnaryOperator addBase = x -> x + base;
        System.out.println("捕获 effectively final: " + addBase.applyAsInt(5));

        // 字段不受此限制：lambda 通过 this 访问外部实例字段
        Runnable printField = () -> System.out.println(
                "lambda 内 this.field = " + this.field + "，this 类型 = " + this.getClass().getSimpleName());
        printField.run();

        // 对比匿名内部类：匿名类有自己的 this，会遮蔽外部 this
        Runnable anon = new Runnable() {
            private String field = "匿名类字段";

            @Override
            public void run() {
                System.out.println(
                        "匿名类 this.field = " + this.field + "，this 类型 = " + this.getClass().getSimpleName());
            }
        };
        anon.run();

        // 捕获的是引用：引用不能重新赋值，但对象内容可变
        List<String> list = new ArrayList<>();
        Runnable add = () -> list.add("元素");
        add.run();
        System.out.println("捕获可变对象内容: " + list);
    }
}
