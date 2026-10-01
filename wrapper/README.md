# 包装类与装箱（Wrapper）

Java 为 8 种基本类型提供了对应的包装类（`Integer`、`Double`…），让基本类型能参与泛型、集合、反射，也能表达“无值”（`null`）。本模块覆盖包装类常量、自动装箱/拆箱、Integer 缓存陷阱、解析工具与比较方式。示例面向 Java 8+。

## 1. 包装类基础

| 基本类型 | 包装类 | 说明 |
|----------|--------|------|
| `byte` | `Byte` | |
| `short` | `Short` | |
| `int` | `Integer` | 名字最不一致，注意 |
| `long` | `Long` | |
| `float` | `Float` | |
| `double` | `Double` | |
| `char` | `Character` | |
| `boolean` | `Boolean` | |

包装类是不可变对象，提供常量与工具方法：

```java
System.out.println(Integer.MAX_VALUE);  // 2147483647
System.out.println(Integer.MIN_VALUE);  // -2147483648
System.out.println(Integer.SIZE);       // 32（比特位数）
System.out.println(Integer.BYTES);      // 4
System.out.println(Integer.TYPE);       // int.class（基本类型的 Class 对象）
```

常用常量还有 `Double.MAX_VALUE`、`Double.NaN`、`Character.MIN_VALUE` 等。`Integer.TYPE` 与 `Integer.class` 不同：前者是 `int` 的 Class，后者是包装类的 Class。

## 2. 自动装箱与拆箱（Java 5+）

编译器在基本类型与包装类之间自动转换，本质是语法糖：

```java
Integer i = 100;   // 自动装箱 → Integer.valueOf(100)
int n = i;         // 自动拆箱 → i.intValue()

List<Integer> list = new ArrayList<>();
list.add(1);       // 装箱存入
int first = list.get(0); // 拆箱取出
```

注意：

- **拆箱 `null` 抛 `NullPointerException`**：包装类型可以为 null，拆箱前要判空
- 装箱拆箱有对象创建开销，热点循环里优先用基本类型
- `Integer` 赋给 `long` 会先拆箱再加宽

## 3. Integer 缓存陷阱

`Integer.valueOf` 对 **-128 ~ 127** 的值返回缓存对象（`IntegerCache`），超出范围才新建：

```java
Integer a = 127;
Integer b = 127;
Integer c = 128;
Integer d = 128;
System.out.println(a == b);   // true，缓存同一对象
System.out.println(c == d);   // false，两个新对象
System.out.println(c.equals(d)); // true
```

`Byte`/`Short`/`Integer`/`Long` 都缓存 -128~127，`Character` 缓存 0~127，`Boolean` 只有两个常量。**包装类型判相等一律用 `equals`**，`==` 只比较引用。

`new Integer(...)` 已废弃（Java 9+ `@Deprecated`），统一用 `Integer.valueOf` 或自动装箱。

## 4. 解析与转换

```java
int n = Integer.parseInt("42");          // 返回 int，最常用
int n2 = Integer.parseInt("1010", 2);    // 按二进制解析 → 10
Integer obj = Integer.valueOf("42");     // 返回 Integer（命中缓存）
int n3 = Integer.decode("0x1F");         // 支持 0x / 0 / # 前缀 → 31
double d = Double.parseDouble("3.14");
boolean flag = Boolean.parseBoolean("true");
```

解析失败抛 `NumberFormatException`。反向转换：

```java
String s = Integer.toString(255);
String hex = Integer.toHexString(255);   // ff
String bin = Integer.toBinaryString(10); // 1010
```

## 5. 比较与选择

```java
Integer a = 200, b = 200;
System.out.println(a == b);             // false（超出缓存）
System.out.println(a.equals(b));        // true
System.out.println(Integer.compare(a, b)); // 0
System.out.println(a.compareTo(b));     // 0
```

各包装类都有 `compare(x, y)` 静态方法与 `compareTo` 实例方法，`Float`/`Double` 对 `NaN` 的排序位置与 `Collections.sort` 一致（`NaN` 视为最大）。

**何时用包装类、何时用基本类型**：

| 场景 | 选择 |
|------|------|
| 局部变量、算术运算、循环变量 | 基本类型（无对象开销） |
| 泛型参数、集合元素 | 包装类（`List<Integer>` 不能用 `List<int>`） |
| 需要表达“无值” | 包装类（`null`） |
| 反射、序列化字段要区分缺失 | 包装类 |

数据库实体字段常用包装类，就是为了区分“没填”（`null`）和“值为 0”。

## 6. 运行本模块

**VS Code**：打开 `src/com/learn/wrapper/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `WrapperBasics.java` | 8 个包装类与常用常量 |
| `Autoboxing.java` | 自动装箱/拆箱与 null 拆箱 NPE |
| `CachePitfall.java` | Integer 缓存与 == 陷阱 |
| `ParseUtils.java` | parseInt / valueOf / decode |
| `WrapperCompare.java` | equals / compare / compareTo 与选型建议 |

## 易错点

1. `Integer a = 128; Integer b = 128; a == b` 是 `false`——超出缓存范围是两个对象
2. `Integer` 判相等用 `equals`，`==` 只在缓存范围内碰巧为 true，不可依赖
3. 拆箱 `null` 会抛 `NullPointerException`，从数据库/Map 取包装类先判空
4. `Integer.parseInt` 返回 `int`，`valueOf` 返回 `Integer`，别搞混
5. `new Integer(...)` 已废弃，用 `Integer.valueOf` 或自动装箱
6. `List<int>` 编译失败，泛型参数必须是包装类 `List<Integer>`
7. `Double` 比较慎用 `==`：浮点精度问题请用误差范围或 `Double.compare`
