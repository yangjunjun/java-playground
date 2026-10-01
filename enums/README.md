# 枚举（Enum）

枚举是 Java 5 引入的特殊类：用有限个**命名常量**表达固定集合（星期、状态、操作符）。每个常量是该类的单例实例，天然线程安全、可序列化、可比较。本模块覆盖枚举基础、字段/方法、抽象方法与接口、EnumSet/EnumMap、常见陷阱。

## 1. 枚举本质

`enum` 是“继承 `java.lang.Enum` 的 final 类”，常量是 `public static final` 实例：

```java
enum Season { SPRING, SUMMER, AUTUMN, WINTER }
```

- 不能 `new Season(...)`，也不能被继承（已是 final）。
- `Season.class.getSuperclass()` 是 `java.lang.Enum`。

## 2. name / ordinal / values / valueOf

| 方法 | 说明 |
|------|------|
| `name()` | 常量声明名（字符串），别轻易改名 |
| `ordinal()` | 声明顺序（从 0 开始），**仅用于 EnumSet/EnumMap 内部，不要当业务值** |
| `values()` | 按声明顺序返回所有常量（数组） |
| `valueOf(String)` | 按名解析；找不到抛 `IllegalArgumentException`（区分大小写） |

```java
Season s = Season.SUMMER;
s.name();          // "SUMMER"
s.ordinal();       // 1
Season.values();   // Season[4]
Season.valueOf("WINTER");
```

## 3. 字段、构造器与方法

枚举可以有字段、方法和构造器（构造器**只能 private**，可省略修饰符）：

```java
enum Planet {
    MERCURY(3.303e+23, 2.4397e6),
    EARTH(5.976e+24, 6.37814e6);

    private final double mass, radius;
    Planet(double mass, double radius) { // private 可省略
        this.mass = mass; this.radius = radius;
    }
    double surfaceGravity() { /* ... */ return 0; }
}
```

## 4. 抽象方法与接口

- 枚举可声明抽象方法，由**常量特定类体**分别实现；
- 枚举**不能 extends 类**（隐式继承 `Enum`），但**可以 implements 接口**。

```java
enum Operation {
    PLUS {
        double apply(double x, double y) { return x + y; }
    },
    MINUS {
        double apply(double x, double y) { return x - y; }
    };
    abstract double apply(double x, double y);
}

interface Describable { String describe(); }
enum Color implements Describable {
    RED { public String describe() { return "红色"; } }
}
```

## 5. EnumSet 与 EnumMap

| 类型 | 实现 | 优势 |
|------|------|------|
| `EnumSet<E>` | 位向量 | 比 `HashSet` 更快更省内存 |
| `EnumMap<K extends Enum<K>, V>` | 数组 | 比 `HashMap` 更快，按 ordinal 索引 |

```java
EnumSet<Day> weekend = EnumSet.of(Day.SAT, Day.SUN);
EnumMap<Day, String> plan = new EnumMap<>(Day.class);
```

## 6. switch 中的枚举

```java
switch (s) {
    case CIRCLE -> "圆形";   // 可省略枚举名前缀
    case RECT   -> "矩形";
}
```

switch 模式匹配（Java 21+）可与 `when` 守卫组合：

```java
String desc = switch (obj) {
    case Shape c when c == Shape.CIRCLE -> "圆形常量";
    case Shape r -> "其他形状: " + r;
    default -> "非 Shape";
};
```

## 7. 何时用枚举 vs 常量

| 场景 | 建议 |
|------|------|
| 固定、互斥取值（星期、状态、操作符） | **enum** |
| 需要附加行为/数据、按常量分派多态 | **enum + 常量特定类体** |
| 连续数值区间、外部可扩展配置 | 普通类 / int，不要硬凑枚举 |
| int 常量 `STATUS_ON = 1` | 缺乏类型安全，可传任意 int；换成 enum |

比较与序列化：枚举**天然实现 `Comparable`（按 ordinal）和 `Serializable`**，序列化有特殊保护（反序列化不产生新实例）。`==` 与 `equals()` 等价。

## 8. 运行本模块

**VS Code**：打开 `src/com/learn/enums/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `EnumBasics.java` | name/ordinal/values/valueOf |
| `EnumWithFields.java` | 字段、构造器、方法 |
| `EnumMethods.java` | compareTo、equals、可序列化/可比较 |
| `EnumAbstractAndInterface.java` | 常量特定类体、实现接口 |
| `EnumPatterns.java` | EnumSet/EnumMap、switch 与模式匹配 |
| `EnumPitfalls.java` | ordinal 陷阱与选型建议 |

## 易错点

1. 用 `ordinal()` 当业务编码/持久化值 → 常量重排后数据错乱（应显式 code 字段）
2. `valueOf("winter")` 失败 → 区分大小写，必须与声明名完全一致
3. 给枚举写 `extends` 或 public 构造器 → 编译错误（隐式继承 Enum、构造器只能 private）
4. 期望 `switch` 自动覆盖新常量却写了 `default` 吞掉 → 新增常量时无感知（可考虑无 default 的穷尽 switch）
5. 拿 `EnumMap` 存非枚举键、或拿 `HashMap<Day,...>` 追求性能 → 用 EnumMap 更合适
6. 把可扩展的外部值（用户自定义标签）做成枚举 → 无法扩展，应改为普通类/配置
