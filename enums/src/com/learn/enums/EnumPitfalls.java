package com.learn.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 枚举陷阱与选型建议：ordinal 陷阱、何时用枚举 vs 常量。
 */
public class EnumPitfalls {

    enum Level {
        LOW, MEDIUM, HIGH
    }

    /** 反例：把 ordinal 当业务值存储 —— 常量重排序后数据就错了 */
    static int badEncode(Level level) {
        return level.ordinal();
    }

    /** 正例：显式业务编码，与声明顺序解耦 */
    enum LevelV2 {
        LOW(10), MEDIUM(20), HIGH(30);

        private final int code;

        LevelV2(int code) {
            this.code = code;
        }

        int code() {
            return code;
        }
    }

    /** 反例：用 int 常量表示状态，缺乏类型安全 */
    static final int STATUS_ON = 1;
    static final int STATUS_OFF = 0;

    public static void demo() {
        System.out.println("-- 陷阱1：ordinal() 会随常量重排变化 --");
        System.out.println("现在 badEncode(HIGH) = " + badEncode(Level.HIGH)
                + "；若在 HIGH 前插入常量，编码就变了 —— 不要用 ordinal 存库/通信");

        System.out.println("-- 正例：显式编码 --");
        System.out.println("LevelV2.HIGH.code() = " + LevelV2.HIGH.code() + "（与声明顺序无关）");

        System.out.println("-- 陷阱2：int 常量没有类型检查 --");
        Map<Integer, String> table = new HashMap<>();
        table.put(STATUS_ON, "开");
        table.put(STATUS_OFF, "关");
        // process(42) 也能编译通过；枚举则编译期拦住
        System.out.println("int 常量 map = " + table + "（可传任意 int，枚举不行）");

        System.out.println("-- 选型建议 --");
        System.out.println("1. 固定集合、互斥取值（星期、状态、操作符）→ 用 enum");
        System.out.println("2. 需要附加行为/数据、多态分支 → 用 enum + 常量特定类体");
        System.out.println("3. 数值区间、连续量、可扩展外部配置 → 用普通类/int，不要硬凑枚举");
        System.out.println("4. 枚举不能 extends（已继承 Enum），但可 implements 接口");
    }
}
