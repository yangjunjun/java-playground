package com.learn.enums;

/**
 * 枚举的抽象方法与接口实现：常量特定类体（constant-specific class body）。
 */
public class EnumAbstractAndInterface {

    /** 枚举可声明抽象方法，每个常量用常量特定类体给出实现 */
    enum Operation {
        PLUS {
            @Override
            double apply(double x, double y) {
                return x + y;
            }
        },
        MINUS {
            @Override
            double apply(double x, double y) {
                return x - y;
            }
        },
        TIMES {
            @Override
            double apply(double x, double y) {
                return x * y;
            }
        };

        abstract double apply(double x, double y);
    }

    /** 枚举不能 extends 类（已隐式继承 Enum），但可以 implements 接口 */
    interface Describable {
        String describe();
    }

    enum Color implements Describable {
        RED {
            @Override
            public String describe() {
                return "红色：热情";
            }
        },
        GREEN {
            @Override
            public String describe() {
                return "绿色：自然";
            }
        },
        BLUE {
            @Override
            public String describe() {
                return "蓝色：沉静";
            }
        }
    }

    public static void demo() {
        System.out.println("-- 常量特定类体实现抽象方法 --");
        double x = 6, y = 4;
        for (Operation op : Operation.values()) {
            System.out.println("  " + op + "(" + x + ", " + y + ") = " + op.apply(x, y));
        }

        System.out.println("-- 枚举实现接口 --");
        for (Color c : Color.values()) {
            System.out.println("  " + c + " → " + c.describe());
        }

        System.out.println("-- 小结：枚举不能继承类（隐式 extends Enum），但可实现多个接口 --");
    }
}
