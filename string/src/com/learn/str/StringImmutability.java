package com.learn.str;

/**
 * 不可变性：修改返回新对象，循环拼接是性能陷阱。
 */
public class StringImmutability {

    public static void demo() {
        String s = "hello";
        String t = s.concat(" world");
        System.out.println("原串不变: s=" + s);
        System.out.println("concat 返回新对象: t=" + t + ", s==t: " + (s == t));

        String upper = s.toUpperCase();
        System.out.println("toUpperCase 也不改原串: s=" + s + ", upper=" + upper);

        explainLoopTrap();
    }

    private static void explainLoopTrap() {
        // 每次 += 都新建字符串并复制内容，n 轮循环约产生 n(n+1)/2 次复制
        String result = "";
        for (int i = 0; i < 3; i++) {
            result += i;
            System.out.println("第 " + i + " 次拼接后新对象: \"" + result + "\""
                    + " (identity=" + System.identityHashCode(result) + ")");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append(i);
        }
        System.out.println("StringBuilder 只有一个对象: \"" + sb + "\""
                + " (identity=" + System.identityHashCode(sb) + ")");
        System.out.println("结论: 循环拼接一律用 StringBuilder，String += 只适合零星拼接");
    }
}
