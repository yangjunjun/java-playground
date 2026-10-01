package com.learn.exception;

/**
 * try-catch-finally：执行顺序与 finally 中 return 的陷阱。
 */
public class TryCatchFinally {

    /** 正常路径：try → finally */
    static int normal() {
        try {
            System.out.println("try: 正常执行");
            return 1;
        } catch (Exception e) {
            System.out.println("catch: 不会执行");
            return -1;
        } finally {
            // finally 在 return 之前执行，可用于释放资源
            System.out.println("finally: 无论是否异常都会执行");
        }
    }

    /** 异常路径：try → catch → finally */
    static int onException() {
        try {
            System.out.println("try: 即将抛出异常");
            throw new RuntimeException("业务失败");
        } catch (RuntimeException e) {
            System.out.println("catch: 捕获到 " + e.getMessage());
            return -2;
        } finally {
            System.out.println("finally: 异常路径同样执行");
        }
    }

    /** 陷阱：finally 中的 return 会“吞掉” try 中的返回值/异常 */
    static int returnInFinally() {
        try {
            return 10;
        } finally {
            return 20; // 覆盖 try 的返回值，且吞掉未捕获异常 —— 永远不要这样写！
        }
    }

    public static void demo() {
        System.out.println("-- 正常路径返回 " + normal());
        System.out.println("-- 异常路径返回 " + onException());
        System.out.println("-- finally 中 return 覆盖 try 返回值: " + returnInFinally()
                + "（本应返回 10）");
    }
}
