package com.learn.array;

import java.util.Arrays;

/**
 * 多维数组：二维数组、不规则（jagged）数组、深度输出。
 */
public class MultiDimArray {

    public static void demo() {
        int[][] m = new int[2][3];
        m[0][0] = 1;
        m[1][2] = 9;
        System.out.println("m 行数=" + m.length + ", 列数=" + m[0].length);
        System.out.println("deepToString(m)=" + Arrays.deepToString(m));

        int[][] jagged = {
                { 1 },
                { 1, 2 },
                { 1, 2, 3 }
        };
        System.out.println("不规则数组行数=" + jagged.length);
        for (int i = 0; i < jagged.length; i++) {
            System.out.println("  第 " + i + " 行长度 " + jagged[i].length
                    + ": " + Arrays.toString(jagged[i]));
        }

        int[][] partial = new int[2][];
        partial[0] = new int[1];
        partial[1] = new int[4];
        System.out.println("先建行再建列: partial[0].length=" + partial[0].length
                + ", partial[1].length=" + partial[1].length);

        int total = 0;
        for (int[] row : jagged) {
            for (int value : row) {
                total += value;
            }
        }
        System.out.println("嵌套 for-each 求和: " + total);

        int[][][] cube = new int[2][2][2];
        cube[0][0][0] = 7;
        System.out.println("三维数组 cube[0][0][0]=" + cube[0][0][0]
                + ", deepToString=" + Arrays.deepToString(cube));
    }
}
