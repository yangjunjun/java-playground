package com.learn.modern;

/**
 * switch 表达式（Java 14+ 正式）。
 * 支持箭头语法、返回值、yield 语句，且不需要 break 防贯穿。
 */
public class SwitchExpressions {

    enum Day { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }

    public static void demo() {
        Day day = Day.FRIDAY;

        // 改造前：语句式 switch，容易漏 break 导致贯穿
        String oldStyle;
        switch (day) {
            case SATURDAY:
            case SUNDAY:
                oldStyle = "周末";
                break;
            default:
                oldStyle = "工作日";
                break;
        }
        System.out.println("旧写法（语句）: " + oldStyle);

        // 改造后：switch 表达式 + 箭头语法，无需 break
        String kind = switch (day) {
            case SATURDAY, SUNDAY -> "周末";
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "工作日";
        };
        System.out.println("新写法（表达式+箭头）: " + kind);

        // 需要多行逻辑时用 yield 返回值
        int code = switch (day) {
            case MONDAY -> {
                System.out.println("（yield 分支：周一综合症）");
                yield 1;
            }
            case FRIDAY -> {
                System.out.println("（yield 分支：周五快乐）");
                yield 5;
            }
            default -> day.ordinal() + 1;
        };
        System.out.println("yield 返回值: " + code);

        // 传统冒号语法也可以是表达式，但必须覆盖所有情况或有 default
        int num = switch (day.name().charAt(0)) {
            case 'M': yield 1;
            case 'T': yield 2;
            case 'W': yield 3;
            default:  yield 0;
        };
        System.out.println("冒号语法 yield: " + num);

        System.out.println("小结：表达式形式必须穷尽所有分支（枚举全覆盖时可省 default）");
    }
}
