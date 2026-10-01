package com.learn.array;

import java.util.Arrays;

/**
 * 数组复制：clone / System.arraycopy / Arrays.copyOf 与浅拷贝。
 */
public class ArrayCopy {

    static class Box {
        final String label;

        Box(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public static void demo() {
        int[] src = { 1, 2, 3, 4, 5 };

        int[] cloned = src.clone();
        cloned[0] = 99;
        System.out.println("clone 后互不影响: src=" + Arrays.toString(src)
                + ", cloned=" + Arrays.toString(cloned));

        int[] copied = new int[3];
        System.arraycopy(src, 1, copied, 0, 3);
        System.out.println("arraycopy(src,1,copied,0,3): " + Arrays.toString(copied));

        int[] truncated = Arrays.copyOf(src, 3);
        int[] grown = Arrays.copyOf(src, 7);
        int[] ranged = Arrays.copyOfRange(src, 1, 4);
        System.out.println("copyOf(src,3)=" + Arrays.toString(truncated)
                + ", copyOf(src,7)=" + Arrays.toString(grown));
        System.out.println("copyOfRange(src,1,4)=" + Arrays.toString(ranged));

        shallowCopy();
    }

    private static void shallowCopy() {
        Box[] src = { new Box("盒子A"), new Box("盒子B") };
        Box[] copied = src.clone();
        copied[0] = new Box("新盒子");
        System.out.println("替换引用后: src[0]=" + src[0] + ", copied[0]=" + copied[0]);

        Box[] shared = Arrays.copyOf(src, src.length);
        // 浅拷贝：两份数组指向同一批 Box 对象
        System.out.println("copyOf 浅拷贝共享元素对象: shared[1]==src[1] → "
                + (shared[1] == src[1]));
    }
}
