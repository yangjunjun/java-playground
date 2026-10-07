package com.learn.array;

/**
 * 数组基础：声明与初始化、默认值、length、下标遍历、协变陷阱。
 */
public class ArrayBasics {

    public static void demo() {
        int[] a = new int[5];
        int[] b = { 1, 2, 3 };
        int[] c = new int[] { 1, 2 };
        String[] names = new String[3];

        System.out.println("a.length=" + a.length + ", 内容=" + java.util.Arrays.toString(a));
        System.out.println("b=" + java.util.Arrays.toString(b) + ", c=" + java.util.Arrays.toString(c));
        System.out.println("names 默认全 null: " + java.util.Arrays.toString(names));

        double[] ds = new double[2];
        boolean[] flags = new boolean[2];
        char[] chars = new char[2];
        System.out.println("默认值 double=" + ds[0] + ", boolean=" + flags[0]
                + ", char 码点=" + (int) chars[0]);

        b[0] = 10;
        System.out.println("下标赋值后 b[0]=" + b[0] + ", length 仍是 " + b.length);

        System.out.print("经典 for 遍历: ");
        for (int i = 0; i < b.length; i++) {
            System.out.print(b[i] + " ");
        }
        System.out.println();

        System.out.print("for-each 遍历: ");
        for (int value : b) {
            System.out.print(value + " ");
        }
        System.out.println();

        try {
            System.out.println(b[3]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("越界访问抛出: " + e.getClass().getSimpleName());
        }

        covariance();
    }

    private static void covariance() {
        Object[] arr = new String[2];
        arr[0] = "ok";
        System.out.println("协变: String[] 可赋给 Object[], arr[0]=" + arr[0]);
        try {
            // 编译期按 Object[] 放行，运行时按 String[] 检查元素类型
            arr[1] = 100;
        } catch (ArrayStoreException e) {
            System.out.println("存入 Integer 抛出: " + e.getClass().getSimpleName());
        }
    }
}
