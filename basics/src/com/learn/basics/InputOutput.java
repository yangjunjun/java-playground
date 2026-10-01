package com.learn.basics;

import java.util.Scanner;

/**
 * 输入与输出：System.out 三种输出方式 + Scanner 读字符串数据（离线可运行）。
 */
public class InputOutput {

    public static void demo() {
        System.out.print("print 不换行: ");
        System.out.print("A");
        System.out.println(" | println 会换行");

        int total = 42;
        System.out.printf("printf 格式化: %s = %d, 保留两位小数 %.2f%n", "总计", total, 3.14159);

        // 数据预先写进字符串，用 Scanner 扫描，避免依赖键盘输入
        Scanner sc = new Scanner("3 4 3.5\n下一行是文本\n");
        int a = sc.nextInt();
        int b = sc.nextInt();
        double rate = sc.nextDouble();
        sc.nextLine();
        String line = sc.nextLine();
        System.out.println("Scanner 读到: a=" + a + ", b=" + b + ", rate=" + rate
                + ", line=" + line);

        tokenVsLine();
        sc.close();
    }

    private static void tokenVsLine() {
        Scanner sc = new Scanner("100\n完整的一行内容\n");
        int n = sc.nextInt();
        // nextInt 只消费数字，换行符还留在缓冲区，必须先 nextLine 吃掉
        sc.nextLine();
        String text = sc.nextLine();
        System.out.println("next vs nextLine: n=" + n + ", text=" + text);

        Scanner tokens = new Scanner("红 绿 蓝");
        System.out.print("next() 按空白符逐个读: ");
        while (tokens.hasNext()) {
            System.out.print(tokens.next() + "/");
        }
        System.out.println();
        tokens.close();
        sc.close();
    }
}
