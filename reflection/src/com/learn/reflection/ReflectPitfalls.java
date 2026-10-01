package com.learn.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射常见异常：NoSuchMethodException / IllegalAccessException /
 * InvocationTargetException / IllegalArgumentException。
 */
public class ReflectPitfalls {

    public static class Demo {
        public void hello(String s) {
        }

        private void secret() {
            throw new IllegalStateException("内部异常");
        }
    }

    public static void demo() {
        Class<Demo> cls = Demo.class;

        // 1) NoSuchMethodException：参数类型写错/漏写
        try {
            cls.getDeclaredMethod("hello");   // 实际需要 String.class
        } catch (NoSuchMethodException e) {
            System.out.println("NoSuchMethodException: " + e.getMessage());
        }

        // 2) IllegalAccessException：private 未 setAccessible
        try {
            Method secret = cls.getDeclaredMethod("secret");
            secret.invoke(new Demo());
        } catch (IllegalAccessException e) {
            System.out.println("IllegalAccessException: " + e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            System.out.println("InvocationTargetException, 解包: " + e.getCause());
        }

        // 3) InvocationTargetException：目标方法抛异常，必须 getCause()
        try {
            Method secret = cls.getDeclaredMethod("secret");
            secret.setAccessible(true);
            secret.invoke(new Demo());
        } catch (ReflectiveOperationException e) {
            System.out.println("捕获 " + e.getClass().getSimpleName()
                    + ", 真实原因: " + e.getCause());
        }

        // 4) IllegalArgumentException：invoke 参数类型不匹配（RuntimeException，非反射异常）
        try {
            Method hello = cls.getDeclaredMethod("hello", String.class);
            hello.invoke(new Demo(), 123);   // 传了 Integer
        } catch (ReflectiveOperationException e) {
            System.out.println("反射异常: " + e);
        } catch (IllegalArgumentException e) {
            System.out.println("参数不匹配: " + e);
        }

        System.out.println("性能提示: 反射调用比直接调用慢约一个数量级，热点路径慎用");
        System.out.println("模块提示(Java 9+): 跨模块反射 JDK 内部会抛 InaccessibleObjectException");
    }
}
