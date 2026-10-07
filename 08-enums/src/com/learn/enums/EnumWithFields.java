package com.learn.enums;

/**
 * 枚举字段、构造器与方法：给常量附加数据。
 */
public class EnumWithFields {

    enum Planet {
        MERCURY(3.303e+23, 2.4397e6),
        VENUS(4.869e+24, 6.0518e6),
        EARTH(5.976e+24, 6.37814e6),
        MARS(6.421e+23, 3.3972e6);

        private final double mass;   // 字段建议 final
        private final double radius;

        // 枚举构造器：只能是 private（可省略），绝不允许 new Planet(...)
        Planet(double mass, double radius) {
            this.mass = mass;
            this.radius = radius;
        }

        double surfaceGravity() {
            final double G = 6.67300E-11;
            return G * mass / (radius * radius);
        }

        double weight(double otherMass) {
            return otherMass * surfaceGravity();
        }
    }

    enum Status {
        ACTIVE("启用"), DISABLED("停用");

        private final String label;

        Status(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        /** 常见模式：按业务字段反查枚举 */
        static Status fromLabel(String label) {
            for (Status s : values()) {
                if (s.label.equals(label)) {
                    return s;
                }
            }
            throw new IllegalArgumentException("未知状态: " + label);
        }
    }

    public static void demo() {
        System.out.println("-- 常量附加数据 --");
        double earthWeight = 75;
        for (Planet p : Planet.values()) {
            System.out.printf("  %s 上的 %s kg 重 %.2f N%n", p.name(), earthWeight, p.weight(earthWeight));
        }

        System.out.println("-- 枚举方法与反查 --");
        System.out.println("Status.ACTIVE.getLabel() = " + Status.ACTIVE.getLabel());
        System.out.println("fromLabel(\"停用\") = " + Status.fromLabel("停用"));
    }
}
