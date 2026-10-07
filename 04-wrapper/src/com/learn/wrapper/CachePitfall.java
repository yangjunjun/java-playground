package com.learn.wrapper;

/**
 * Integer 缓存陷阱：-128~127 走 IntegerCache，== 与 equals 的差异。
 */
public class CachePitfall {

    public static void demo() {
        Integer a = 127;
        Integer b = 127;
        Integer c = 128;
        Integer d = 128;

        System.out.println("127: a==b " + (a == b) + ", a.equals(b) " + a.equals(b));
        System.out.println("128: c==d " + (c == d) + ", c.equals(d) " + c.equals(d));
        System.out.println("valueOf 对 -128~127 返回缓存对象，范围外才 new");

        Integer cached = Integer.valueOf(100);
        Integer another = 100; // 自动装箱同样走 valueOf
        System.out.println("valueOf 与自动装箱共享缓存: " + (cached == another));

        Integer x = new Integer(200);
        Integer y = new Integer(200);
        // new 出来的对象永远是两份，== 必为 false
        System.out.println("new Integer(200)==: " + (x == y) + "，内容相等用 equals: " + x.equals(y));

        Long l1 = 127L;
        Long l2 = 127L;
        Long l3 = 128L;
        Long l4 = 128L;
        System.out.println("Long 也有缓存: 127 → " + (l1 == l2) + ", 128 → " + (l3 == l4));

        Character c1 = 65;
        Character c2 = 65;
        System.out.println("Character 缓存 0~127: " + (c1 == c2));

        Integer risk = maybeNull();
        // 若 risk 是 null，装箱比较时拆箱会直接 NPE
        if (risk != null && risk == 100) {
            System.out.println("与 int 字面量比较会先拆箱，务必先判 null");
        }
    }

    private static Integer maybeNull() {
        return 100;
    }
}
