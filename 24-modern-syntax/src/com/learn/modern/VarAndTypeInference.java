package com.learn.modern;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * var 局部变量类型推断（Java 10+）。
 * 编译器根据右侧表达式推断类型，字节码里仍是强类型，不是动态类型。
 */
public class VarAndTypeInference {

    // 注意：var 不能用在字段上，下面这行若取消注释会编译失败
    // var field = 42;

    // var 也不能用作方法返回类型，下面这行同样编译失败
    // var method() { return 1; }

    public static void demo() {
        // 改造前：显式类型写全，冗长
        List<String> oldList = new ArrayList<String>();

        // 改造后：右侧已是具体类型，用 var 省掉重复
        var list = new ArrayList<String>();
        list.add("Java");
        System.out.println("var 推断类型: " + list.getClass().getSimpleName() + ", 内容=" + list);

        // 必须有初始化表达式，不能只写 var x;
        // var x;  // 编译错误

        // 也不能只用 null 字面量初始化：类型无法推断
        // var n = null;  // 编译错误
        var n = (String) null; // 需要显式目标类型
        System.out.println("null 需要显式类型: " + n);

        // for-each、try-with-resources、循环里都好用
        for (var entry : Map.of("k1", 1, "k2", 2).entrySet()) {
            System.out.println("for-each 中 var: " + entry.getKey() + " -> " + entry.getValue());
        }

        // 适合“右边一眼能看出类型”的场景；不适合 lambda 参数、方法引用目标
        // var fn = (x) -> x + 1;   // 编译错误：lambda 需要目标类型
        java.util.function.Function<Integer, Integer> fn = x -> x + 1;
        System.out.println("lambda 仍需显式函数式接口类型: " + fn.apply(1));

        // 小结：var 只是语法糖，能少写但别滥用
        System.out.println("小结：var 仅限局部变量、需初始化、不能是 null 字面量、不能用于字段/返回类型");
    }
}
