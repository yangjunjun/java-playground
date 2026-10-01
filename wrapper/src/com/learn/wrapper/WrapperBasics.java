package com.learn.wrapper;

/**
 * 包装类基础：8 个包装类与常用常量。
 */
public class WrapperBasics {

    public static void demo() {
        Byte b = 1;
        Short s = 2;
        Integer i = 3;
        Long l = 4L;
        Float f = 5.0F;
        Double d = 6.0;
        Character c = 'A';
        Boolean flag = Boolean.TRUE;
        System.out.println("8 个包装类: " + b + "," + s + "," + i + "," + l
                + "," + f + "," + d + "," + c + "," + flag);

        System.out.println("Integer.MAX_VALUE=" + Integer.MAX_VALUE);
        System.out.println("Integer.MIN_VALUE=" + Integer.MIN_VALUE);
        System.out.println("Integer.SIZE=" + Integer.SIZE + " 位, BYTES=" + Integer.BYTES);
        System.out.println("Long.MAX_VALUE=" + Long.MAX_VALUE);
        System.out.println("Double.MAX_VALUE=" + Double.MAX_VALUE + ", Double.NaN=" + Double.NaN);

        System.out.println("Integer.TYPE=" + Integer.TYPE + "（基本类型 int 的 Class）");
        System.out.println("Integer.class=" + Integer.class + "（包装类自己的 Class）");
        System.out.println("TYPE == Integer.class ? " + (Integer.TYPE == Integer.class));

        System.out.println("Character 是否字母: " + Character.isLetter('中')
                + ", 是否数字: " + Character.isDigit('7'));
        System.out.println("包装类都是不可变对象，修改值会返回新对象");
    }
}
