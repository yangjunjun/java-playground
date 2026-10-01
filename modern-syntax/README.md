# 现代语法特性（Modern Syntax）

本模块汇总 Java 9 ~ 25 中最常用、最能提升日常编码效率的语言特性：类型推断、switch 表达式、文本块、record、密封类、模式匹配等。每节都给出「改造前 / 改造后」对照，帮助你把老写法迁移到现代写法。

## 1. var 局部变量类型推断（Java 10+）

`var` 让编译器根据右侧表达式推断局部变量类型，字节码仍是强类型，**不是动态类型**：

```java
var list = new ArrayList<String>();   // 推断为 ArrayList<String>
var map = Map.of("k", 1);            // 推断为 Map<String, Integer>
```

**限制（必须记住）**：

| 限制 | 示例 |
|------|------|
| 只能用于局部变量 | 字段 `var field = 1;` 编译失败 |
| 不能作方法返回类型 | `var method() {...}` 编译失败 |
| 必须有初始化表达式 | `var x;` 编译失败 |
| 不能只用 `null` 初始化 | `var n = null;` 编译失败（类型未知） |
| 不能用于 lambda 参数 | `var fn = (x) -> x + 1;` 编译失败 |

适合「右边一眼能看出类型」的场景，滥用会降低可读性。

## 2. switch 表达式（Java 14+）

从语句升级为表达式，支持箭头语法与 `yield`，且不需要 `break`：

```java
// 旧：语句式，容易漏 break 贯穿
String kind;
switch (day) {
    case SATURDAY: case SUNDAY: kind = "周末"; break;
    default: kind = "工作日";
}

// 新：表达式 + 箭头
String kind = switch (day) {
    case SATURDAY, SUNDAY -> "周末";
    case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "工作日";
};

// 多行分支用 yield 返回
int code = switch (day) {
    case MONDAY -> { System.out.println("周一"); yield 1; }
    default    -> 0;
};
```

作为表达式时必须穷尽所有分支；枚举全覆盖可省 `default`。

## 3. 文本块 Text Block（Java 15+）

用三引号 `"""` 包裹多行文本，自动处理缩进与换行，适合 JSON / SQL / HTML：

```java
String json = """
        {
          "name": "张三",
          "age": 18
        }
        """;
```

转义要点：

- `\\` 表示一个反斜杠（Windows 路径必备）
- `\s` 保留一个空格（默认行尾空格会被剥离）
- 收尾 `"""` 的位置决定结尾是否带换行

## 4. record 记录类（Java 16+）

不可变数据载体，自动生成 `equals` / `hashCode` / `toString` / 访问器：

```java
record Point(int x, int y) {
    Point {                       // 紧凑构造器：只写校验
        if (x < 0) throw new IllegalArgumentException("x 不能为负");
    }
    static Point origin() { return new Point(0, 0); }  // 静态工厂
}

interface Shape { double area(); }
record Circle(double r) implements Shape {
    public double area() { return Math.PI * r * r; }
}
```

要点：

- 访问器是 `x()` / `y()`，**不是** `getX()`
- 可以实现接口、写紧凑构造器、加静态工厂
- **不是可变 JavaBean 的替代品**（没有 setter、字段 final）

## 5. sealed 密藏类（Java 17+）

用 `permits` 精确控制哪些类可以继承：

```java
sealed interface Shape permits Circle, Rectangle, Triangle {}
record Circle(double r) implements Shape {}
record Rectangle(double w, double h) implements Shape {}
final record Triangle(double a, double b, double c) implements Shape {}  // final
```

修饰符三选一：`final`（不能再继承）、`sealed`（继续密封）、`non-sealed`（重新放开）。
配合 switch 模式匹配可做**穷尽性检查**——新增子类未覆盖时编译报错。

## 6. 模式匹配（Java 16 / 21+）

**instanceof 模式（Java 16+）**——判断与强转合并：

```java
if (obj instanceof String s) {     // 一步完成
    System.out.println(s.length());
}
```

**switch 模式匹配（Java 21+）**——case 直接绑类型：

```java
String desc = switch (obj) {
    case String s           -> "字符串: " + s;
    case Integer i          -> "整数: " + i;
    case Point(int x, int y) -> "点: " + x + "," + y;   // record 解构
    case null               -> "null";
    default                 -> "其他";
};
```

**record 解构（Java 21+）**：`case Point(int x, int y) ->` 直接拆分量，可嵌套。

**守卫子句 when（Java 21+）**：`case String s when s.length() > 2 ->` 在 case 内加条件。
守卫失败会落到后续 case，而不是抛异常。

**注意**：`case String s when ...` 的守卫是**非穷尽**的，仍需 `default` 或覆盖全部类型。

## 7. 匿名模式与匿名变量（Java 21+ / 22+ 正式）

用 `_` 表示「有意忽略」：

```java
if (o instanceof Point(int x, int _)) {}   // 忽略 y
if (o instanceof Point _) {}               // 整条不绑定
switch (obj) {
    case String _ -> "是字符串";            // 不关心内容
}
try { ... } catch (ArithmeticException _) {}  // 异常也不绑定变量
```

比写未使用变量更诚实、更清晰。

## 8. 构造器中的灵活语句（Java 22+）

`super()` / `this()` 之前可以写普通语句：

```java
User(String raw) {
    String trimmed = raw.trim();          // Java 22+ 允许
    if (trimmed.isEmpty()) throw new IllegalArgumentException("名字不能为空");
    super(trimmed.toLowerCase());
}
```

旧写法只能把校验塞进静态方法再传给 `super()`。

## 9. 更前沿特性（Java 23 ~ 25，仅作了解）

| 特性 | 版本 | 说明 |
|------|------|------|
| 原始类型模式匹配 `case int i ->` | Java 25 **预览** | 需 `--enable-preview`，本模块代码中不启用 |
| 模块导入声明 `import module java.base;` | Java 25 | 可直接写，省去一长串 import |
| 紧凑源文件与实例 main | Java 25 | 单文件可写 `void main()`，无需 class |
| 简化 main、`IO.println` | Java 25 | `java.lang.IO` 提供 `IO.println` |

模块导入示例（Java 25+）：

```java
import module java.base;   // 一次导入 java.base 的所有导出包

void main() {
    IO.println(List.of("a", "b"));
}
```

## 运行本模块

**VS Code**：打开 `src/com/learn/modern/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `VarAndTypeInference.java` | var 类型推断与限制 |
| `SwitchExpressions.java` | switch 表达式、箭头语法、yield |
| `TextBlocks.java` | 文本块、`\\` `\s` 转义 |
| `Records.java` | record 紧凑构造器、访问器、静态工厂 |
| `SealedClasses.java` | sealed permits、穷尽 switch |
| `PatternMatching.java` | instanceof / switch / record 解构 / when |
| `UnnamedPatterns.java` | 匿名模式 `_`、匿名变量 |
| `FlexibleCtorBrief.java` | Java 22+ 构造器前语句 |

## 易错点

1. `var` 不是动态类型：编译期定死类型，也不能用在字段/返回类型上
2. `var x = null;` 编译失败——null 没有可推断的类型
3. switch 表达式漏写 `default` 或未穷尽枚举 → 编译错误（这是特性不是 bug）
4. record 访问器是 `x()` 不是 `getX()`，写 JavaBean 反射时容易踩坑
5. record 无法 set 字段，不要拿它当可变 DTO
6. sealed 的 `permits` 列表漏写新子类 → 编译失败（其实是好事，逼你维护契约）
7. `case String s when ...` 守卫失败会匹配后续 case，不是抛异常
8. 匿名变量 `_` 一旦声明就不能再使用该名字
9. 文本块默认剥掉行尾空格，想保留必须用 `\s`
