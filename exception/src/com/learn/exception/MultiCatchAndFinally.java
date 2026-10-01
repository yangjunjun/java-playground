package com.learn.exception;

/**
 * 多重捕获（multi-catch）、异常重抛/包装、堆栈跟踪。
 */
public class MultiCatchAndFinally {

    static void mightFail(int mode) {
        switch (mode) {
            case 0 -> throw new IllegalArgumentException("参数非法");
            case 1 -> throw new IllegalStateException("状态非法");
            default -> System.out.println("模式 " + mode + " 正常");
        }
    }

    /** 包装异常：保留原始原因（cause），不丢失现场 */
    static void wrapAndRethrow() {
        try {
            mightFail(0);
        } catch (RuntimeException e) {
            // 包装成新的高层异常；initCause/构造器传 cause 保留根因
            throw new IllegalStateException("处理 mode=0 失败", e);
        }
    }

    public static void demo() {
        System.out.println("-- 多重捕获：同分支处理多种异常（Java 7+）--");
        for (int mode : new int[] { 0, 1, 2 }) {
            try {
                mightFail(mode);
            } catch (IllegalArgumentException | IllegalStateException e) {
                // multi-catch 中 e 隐式 final，不能再赋值
                System.out.println("multi-catch 捕获 " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }

        System.out.println("-- 异常包装：保留 cause --");
        try {
            wrapAndRethrow();
        } catch (Exception e) {
            System.out.println("外层: " + e.getMessage());
            System.out.println("根因: " + e.getCause().getMessage());
        }

        System.out.println("-- 堆栈跟踪（这里截取前几行展示）--");
        try {
            mightFail(1);
        } catch (Exception e) {
            StackTraceElement[] stack = e.getStackTrace();
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            for (int i = 0; i < Math.min(3, stack.length); i++) {
                System.out.println("    at " + stack[i]);
            }
        }
    }
}
