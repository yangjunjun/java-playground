# 字符串与文本（String）

字符串是 Java 最常用的类型。本模块覆盖 `String` 的常量池与不可变性、`StringBuilder`、格式化，以及 Java 11~15 引入的现代文本 API（文本块、`strip`/`isBlank`/`lines`/`repeat` 等）。底层细节：Java 9+ 的 compact strings 会用 `byte[]` 存储纯 Latin-1 字符串以省内存；Java 18+（JEP 400）默认字符集统一为 UTF-8。

## 1. String 基础

字符串字面量进入**常量池**（string pool），相同内容共享同一对象；`new String("...")` 一定在堆上新建对象：

```java
String a = "hello";
String b = "hello";           // 与 a 同一个池中对象
String c = new String("hello"); // 堆上的新对象
System.out.println(a == b);      // true，同一对象
System.out.println(a == c);      // false，不同对象
System.out.println(a.equals(c)); // true，内容相同
```

**规则**：比较字符串内容用 `equals`（或 `equalsIgnoreCase`），`==` 比的是引用地址。`intern()` 可手动把字符串放入池中并返回池内引用。

常用方法速查：

| 方法 | 作用 |
|------|------|
| `length()` / `isEmpty()` | 长度 / 是否空串 |
| `charAt(i)` | 第 i 个字符 |
| `indexOf("x")` / `lastIndexOf` | 首次 / 最后出现位置，找不到返回 -1 |
| `substring(1, 4)` | 子串 `[1, 4)` |
| `replace("a", "b")` | 替换全部匹配 |
| `split(",")` | 按正则分割为 `String[]` |
| `contains` / `startsWith` / `endsWith` | 包含 / 前缀 / 后缀 |
| `toUpperCase` / `toLowerCase` | 大小写转换 |

## 2. 不可变性

`String` 是不可变类：任何“修改”都会返回**新对象**，原字符串不变：

```java
String s = "hello";
String t = s.concat(" world");
System.out.println(s);       // hello —— 原对象不变
System.out.println(s == t);  // false —— t 是新对象
```

不可变带来的好处：常量池可安全共享、天然线程安全、`hashCode` 可缓存（`HashMap` 的 key 好用）。

代价：在循环里 `s += "x"` 会产生大量中间对象，时间复杂度 O(n²)，拼接循环请改用 `StringBuilder`。

## 3. StringBuilder 与 StringBuffer

可变字符序列，提供 `append` / `insert` / `delete` / `reverse` 等原地修改：

```java
StringBuilder sb = new StringBuilder("hello");
sb.append(" world").insert(0, ">> ").delete(3, 5).reverse();
String result = sb.toString();
```

| 类型 | 线程安全 | 性能 | 适用 |
|------|----------|------|------|
| `StringBuilder` | 否 | 高 | 单线程拼接（默认选择） |
| `StringBuffer` | 是（方法 synchronized） | 略低 | 多线程共享同一缓冲区 |

`StringBuilder` 初始容量默认 16，`ensureCapacity` / 构造参数可减少扩容拷贝；`length()` 是字符数，`capacity()` 是底层数组容量。

## 4. 文本块（Java 15+）

用 `"""` 书写多行字符串，自动去掉公共缩进（incidental whitespace），适合 JSON / SQL / HTML：

```java
String json = """
        {
            "name": "张三",
            "age": 18
        }
        """;
```

细节：

- 结束 `"""` 的缩进决定“公共前缀”被剥掉多少；`stripIndent()` 可对运行时拼出的多行字符串做同样处理
- 行尾 `\` 表示**合并下一行**（去掉换行）
- `\s` 显式保留一个空格（文本块默认会去掉行尾空白）
- `"` 在文本块里通常无需转义；`\"""` 可表示三个引号
- 结束符写在内容行尾时，字符串不会以换行结尾

```java
String merged = """
        第一行\
        紧接第二行\
        结束""";            // → "第一行紧接第二行结束"
String spaces = "A\sB\s";   // \s 显式保留空格
```

## 5. 格式化与 join

`String.format` / `String.formatted`（Java 15+）按格式串生成字符串：

```java
String f = String.format("%s 年龄 %d 岁，成绩 %.2f", "张三", 18, 95.5);
String g = "左对齐[%-8s]|补零[%05.2f]".formatted("hi", 3.14159);
```

常用占位符：`%s` 字符串、`%d` 整数、`%f` 浮点、`%x` 十六进制、`%n` 平台换行、`%b` 布尔。

`String.join` 用分隔符连接多个片段（Java 8+）：

```java
String joined = String.join(", ", "红", "绿", "蓝"); // 红, 绿, 蓝
```

`translateEscapes()`（Java 15+）把字符串里“字面的”转义序列真正翻译成控制字符。

## 6. Java 11+ 实用 API

| API | 版本 | 作用 |
|-----|------|------|
| `strip()` / `stripLeading()` / `stripTrailing()` | Java 11 | 按 `Character.isWhitespace` 去空白（含全角空格；U+00A0 不算），比 `trim` 更彻底 |
| `isBlank()` | Java 11 | 是否为空或全空白 |
| `lines()` | Java 11 | 按行拆为 `Stream<String>` |
| `repeat(n)` | Java 11 | 重复 n 次 |
| `indent(n)` | Java 12 | 整体缩进（负数尝试去除缩进） |
| `transform(f)` | Java 12 | 用函数变换字符串 |
| `stripIndent()` | Java 15 | 去掉公共缩进；**以换行结尾的字符串会跳过去缩进** |
| `translateEscapes()` | Java 15 | 翻译转义序列 |

```java
System.out.println("  a  ".isBlank());     // false
System.out.println(" \t ".isBlank());      // true
System.out.println("ab".repeat(3));        // ababab
"红\n绿".lines().forEach(System.out::println);
```

`trim()` 只去掉 `<= U+0020` 的空白；处理全角空格等 Unicode 空白用 `strip()`（`Character.isWhitespace` 判定，U+00A0 不换行空格不在其列）。

## 7. 运行本模块

**VS Code**：打开 `src/com/learn/str/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `StringBasics.java` | 常量池、== 与 equals、常用方法 |
| `StringImmutability.java` | 不可变性与循环拼接陷阱 |
| `StringBuilderDemo.java` | StringBuilder / StringBuffer |
| `TextBlocks.java` | 文本块、缩进、`\s`、`\"`、合并行 |
| `StringFormat.java` | format / formatted / join |
| `StringJava11Plus.java` | strip / isBlank / lines / repeat 等新 API |

## 易错点

1. `==` 比较两个字符串变量比的是引用，内容比较必须用 `equals`
2. 循环里 `s += piece` 性能差，应改用 `StringBuilder`
3. `substring` 的结束下标**不包含**，`substring(1, 4)` 取下标 1、2、3
4. `split` 的参数是**正则**，按 `.` 分割要写 `split("\\.")`
5. 文本块的缩进由结束 `"""` 的位置决定，对不齐会多出前导空格
6. `String.formatted` 是实例方法、`String.format` 是静态方法，别混用形式
7. `trim()` 去不掉全角空格，需用 `strip()`；但 U+00A0 不换行空格两者都去不掉
8. `stripIndent()` 对**以换行结尾**的字符串只清理行尾空白、不去缩进，演示时注意结尾
