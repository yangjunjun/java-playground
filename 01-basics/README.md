# 语言基础（Basics）

本模块覆盖 Java 语法的基石：变量与基本类型、运算符、控制流程与循环、输入输出。无论写业务代码还是读框架源码，这些规则处处生效。示例面向 Java 8+，涉及新特性时标注引入版本。

## 1. 变量与基本类型

Java 有 8 种基本类型（primitive）：

| 类型 | 位数 | 说明 |
|------|------|------|
| `byte` | 8 | -128 ~ 127 |
| `short` | 16 | -32768 ~ 32767 |
| `int` | 32 | 约 ±21 亿，**整数字面量默认类型** |
| `long` | 64 | 更大整数，字面量加 `L` |
| `float` | 32 | 单精度浮点，字面量加 `F` |
| `double` | 64 | 双精度浮点，**小数字面量默认类型** |
| `char` | 16 | 单个 Unicode 字符，如 `'A'`、`'中'` |
| `boolean` | — | 只有 `true` / `false`，不能当 0/1 用 |

字面量写法（二进制、下划线为 Java 7+）：

```java
int binary = 0b1010;      // 二进制
int octal  = 017;         // 八进制，前缀 0
int hex    = 0xFF;        // 十六进制，前缀 0x
int n      = 1_000_000;   // 下划线只提高可读性，编译后消失
long big   = 3_000_000_000L;  // 超出 int 范围必须加 L
float f    = 3.14F;           // 不加 F 会被当作 double
char c     = '\u4E2D';        // Unicode 转义，即“中”
```

**默认值**：字段（成员变量）有默认值——数值为 `0`、浮点为 `0.0`、`char` 为 `'\u0000'`、`boolean` 为 `false`、引用类型为 `null`。**局部变量没有默认值**，未赋值就使用直接编译报错。

### 类型转换

加宽转换自动完成且安全：`byte → short → int → long → float → double`，`char` 可加宽为 `int`。

```java
int i = 10;
long l = i;          // 自动加宽
double d = 3.99;
int x = (int) d;     // 强制窄化：截断小数 → 3（不是四舍五入）
byte b = (byte) 130; // 溢出按补码回绕 → -126
```

### var 局部变量类型推断（Java 10+）

```java
var name = "Java";              // 推断为 String，不是动态类型
var count = 42;                 // 推断为 int
var list = new java.util.ArrayList<String>();
```

`var` 只能用于**有初始值的局部变量**，不能用于字段、方法参数、返回类型。switch 表达式、模式匹配等更多现代语法见 `modern-syntax` 模块。

## 2. 运算符

| 类别 | 运算符 | 说明 |
|------|--------|------|
| 算术 | `+ - * / %`、`++ --` | 整数除法截断：`7 / 2 == 3` |
| 关系 | `== != < > <= >=` | 结果为 `boolean` |
| 逻辑 | `&& \|\| !` | **短路**：左边能定结果时右边不执行 |
| 位运算 | `& \| ^ ~` | 按位与/或/异或/取反 |
| 移位 | `<< >> >>>` | `>>>` 无符号右移，高位补 0 |
| 三元 | `条件 ? a : b` | 两个分支类型需能统一 |
| 判断 | `instanceof` | 运行时类型判断（Java 16+ 支持模式变量） |
| 赋值 | `= += -= *= /= %=` 等 | 复合赋值自带隐式强转 |

```java
int max = a > b ? a : b;
boolean ok = (x > 0) && (10 / x > 1); // x<=0 时右边不会执行
if (obj instanceof String s) {        // Java 16+ 模式匹配
    System.out.println(s.length());
}
```

位运算与移位是整数的二进制操作，`~` 是按位取反（`~0 == -1`）；`>>` 右移保留符号位，`>>>` 高位一律补 0。

## 3. 控制流程与循环

条件分支 `if / else if / else`；多分支用 `switch`（本模块演示**传统写法**，switch 表达式属于 `modern-syntax`）：

```java
switch (day) {
    case 1:
        System.out.println("星期一");
        break;               // 少写 break 会“贯穿”到下一个 case
    case 2:
    case 3:                  // 多个标签共享一段逻辑
        System.out.println("周中");
        break;
    default:
        System.out.println("其他");
}
```

传统 `switch` 支持 `int`/`char`/`String`（Java 7+）与枚举，**没有 break 会继续执行下一个 case**（fall-through）。

循环四件套：

```java
for (int i = 0; i < 3; i++) { }        // 已知次数
while (cond) { }                       // 先判断后执行
do { } while (cond);                   // 先执行后判断，至少一次
for (String s : list) { }              // for-each，拿不到下标、改不了元素
```

`break` 跳出当前循环，`continue` 进入下一轮；带标签可跳出外层：

```java
outer:
for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
        if (j == 1) continue outer; // 直接进入外层下一轮
        if (i == 2) break outer;    // 直接结束外层循环
    }
}
```

## 4. 输入与输出

输出用 `System.out`：`print`（不换行）、`println`（换行）、`printf`（格式化）：

```java
System.out.print("结果: ");
System.out.println(42);
System.out.printf("%s = %d%n", "总计", 42);
```

输入用 `java.util.Scanner`（示例把数据写在字符串里，离线可运行）：

```java
Scanner sc = new Scanner("3 4\n一行文本\n");
int a = sc.nextInt();
int b = sc.nextInt();
sc.nextLine();               // 吃掉 nextInt 残留的换行
String line = sc.nextLine();
```

`next()` 读到空白符为止，`nextLine()` 读一整行；`nextInt()`/`nextDouble()` 读数值。读完数值再读行时，必须先 `nextLine()` 清掉缓冲区里的换行。

## 5. 运行本模块

**VS Code**：打开 `src/com/learn/basics/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `VariablesAndTypes.java` | 8 种基本类型、字面量、默认值、类型转换、var |
| `Operators.java` | 算术、比较、短路逻辑、位运算、移位、三元、instanceof |
| `ControlFlow.java` | if/switch、四种循环、break/continue/标签 |
| `InputOutput.java` | System.out 输出与 Scanner 字符串输入 |

## 易错点

1. `float f = 3.14;` 编译失败——小数字面量是 `double`，必须写 `3.14F` 或强制 `(float)`
2. `130` 强转 `byte` 得到 `-126`：强制窄化不报错，只按补码回绕
3. 局部变量不赋值就使用会编译错误，不要指望“默认 0”
4. `7 / 2` 是 `3` 而不是 `3.5`；想保留小数要有一个运算数是浮点
5. 传统 `switch` 少写 `break` 会贯穿执行下一个 case
6. `nextInt()` 后直接 `nextLine()` 常读到空行——缓冲区里还留着换行
7. `==` 比较引用类型时比的是地址，比较字符串内容请用 `equals`
