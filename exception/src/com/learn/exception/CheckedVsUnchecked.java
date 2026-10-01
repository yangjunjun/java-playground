package com.learn.exception;

/**
 * Throwable 层次：Error / Exception / 受检异常 / 非受检异常（RuntimeException）。
 */
public class CheckedVsUnchecked {

    /**
     * Throwable
     * ├── Error               —— JVM 级严重错误（OutOfMemoryError 等），不该捕获
     * └── Exception
     *     ├── 受检异常（IOException、SQLException 等）—— 编译器强制处理
     *     └── RuntimeException —— 非受检（NPE、ISE 等），可不声明
     */

    /** 受检异常：要么 try-catch，要么 throws 声明，否则编译不过 */
    static String readConfig(boolean ok) throws java.io.IOException {
        if (!ok) {
            throw new java.io.IOException("配置文件不存在");
        }
        return "配置内容";
    }

    /** 非受检异常：编译器不强制处理 */
    static int divide(int a, int b) {
        return a / b; // b == 0 时抛出 ArithmeticException（RuntimeException 子类）
    }

    public static void demo() {
        System.out.println("-- 受检异常：必须显式处理 --");
        try {
            readConfig(false);
        } catch (java.io.IOException e) {
            System.out.println("捕获受检异常: " + e.getMessage());
        }

        System.out.println("-- 非受检异常：可不声明，但要防 --");
        try {
            divide(1, 0);
        } catch (ArithmeticException e) {
            System.out.println("捕获非受检异常: " + e);
        }

        System.out.println("-- Error 通常不该捕获（示例仅打印类层次）--");
        System.out.println("OutOfMemoryError extends Error → " + OutOfMemoryError.class.getSuperclass().getSimpleName());
        System.out.println("RuntimeException extends Exception → "
                + RuntimeException.class.getSuperclass().getSimpleName());
    }
}
