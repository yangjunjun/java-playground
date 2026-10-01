package com.learn.enums;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * EnumSet / EnumMap 与 switch 中的枚举（含 Java 21+ 模式匹配简述）。
 */
public class EnumPatterns {

    enum Day {
        MON, TUE, WED, THU, FRI, SAT, SUN
    }

    enum Shape {
        CIRCLE, RECT, TRIANGLE
    }

    public static void demo() {
        System.out.println("-- EnumSet：位向量实现，极省空间与时间 --");
        EnumSet<Day> weekend = EnumSet.of(Day.SAT, Day.SUN);
        EnumSet<Day> weekdays = EnumSet.complementOf(weekend);
        System.out.println("weekend = " + weekend);
        System.out.println("weekdays = " + weekdays);

        System.out.println("-- EnumMap：数组实现，比 HashMap 更快 --");
        EnumMap<Day, String> plan = new EnumMap<>(Day.class);
        plan.put(Day.MON, "站立会");
        plan.put(Day.FRI, "周报");
        for (Map.Entry<Day, String> e : plan.entrySet()) {
            System.out.println("  " + e.getKey() + " → " + e.getValue());
        }

        System.out.println("-- switch 中使用枚举（可省略枚举名前缀）--");
        for (Shape s : Shape.values()) {
            String desc = switch (s) {
                case CIRCLE -> "圆形";
                case RECT -> "矩形";
                case TRIANGLE -> "三角形";
            };
            System.out.println("  " + s + " → " + desc);
        }

        System.out.println("-- switch 模式匹配（Java 21+）：枚举可与 when 守卫组合 --");
        Object shape = Shape.CIRCLE;
        String desc = switch (shape) {
            case Shape c when c == Shape.CIRCLE -> "对象是圆形常量";
            case Shape r -> "其他形状: " + r;
            default -> "不是 Shape";
        };
        System.out.println("  " + desc);
    }
}
