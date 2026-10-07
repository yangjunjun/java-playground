package com.learn.basics;

/**
 * 运算符：算术、关系、短路逻辑、位运算、移位、三元、instanceof。
 */
public class Operators {

    static class Animal {
    }

    static class Dog extends Animal {
    }

    public static void demo() {
        int a = 7;
        int b = 2;
        System.out.println("算术: 7+2=" + (a + b) + ", 7-2=" + (a - b)
                + ", 7*2=" + (a * b) + ", 7/2=" + (a / b) + ", 7%2=" + (a % b));
        System.out.println("浮点除法: 7.0/2=" + (7.0 / b));

        int x = 5;
        System.out.println("自增: x=" + x + ", x++=" + (x++) + "（先用后加），现在 x=" + x);
        System.out.println("++x=" + (++x) + "（先加后用）");

        System.out.println("关系: 7>2=" + (a > b) + ", 7==7=" + (a == 7) + ", 7!=2=" + (a != b));

        // 短路：左边为 false 时右边不再求值
        int denominator = 0;
        boolean safe = denominator != 0 && 10 / denominator > 1;
        System.out.println("短路与: denominator=0 时 10/denominator 未执行, safe=" + safe);
        boolean either = a > 100 || b == 2;
        System.out.println("短路或: a>100 为假，仍判断 b==2 → " + either);

        int p = 0b1111_0000;
        System.out.println("位运算: p=" + p
                + ", p&0x0F=" + (p & 0x0F)
                + ", p|0x0F=" + (p | 0x0F)
                + ", p^0xFF=" + (p ^ 0xFF)
                + ", ~0=" + (~0));
        System.out.println("移位: p<<2=" + (p << 2) + ", p>>2=" + (p >> 2) + ", p>>>2=" + (p >>> 2));
        int neg = -8;
        // >> 保留符号位，>>> 高位补 0，负数无符号右移会变成很大的正数
        System.out.println("负数移位: -8>>1=" + (neg >> 1) + ", -8>>>1=" + (neg >>> 1));

        int max = a > b ? a : b;
        System.out.println("三元: max(7,2)=" + max);

        Object obj = new Dog();
        System.out.println("instanceof: obj 是 Animal? " + (obj instanceof Animal));
        if (obj instanceof Dog) {
            System.out.println("传统写法: obj 是 Dog");
        }
        if (obj instanceof Dog d2) {
            // Java 16+ 模式匹配，d2 作用域内可直接当 Dog 用
            System.out.println("模式匹配: 直接得到变量 d2 = " + d2.getClass().getSimpleName());
        }

        int n = 3;
        n += 5;
        System.out.println("复合赋值: n+=5 后 n=" + n);
    }
}
