# 正则表达式（Regex）

正则表达式（Regular Expression）用一套模式描述文本结构。Java 通过 `java.util.regex` 包提供支持：`Pattern` 表示编译后的正则，`Matcher` 在目标输入串上执行匹配、查找与替换。适合做校验、提取、拆分、脱敏等文本处理。

## 1. Pattern 与 Matcher

`Pattern.compile` 把正则编译成对象，`matcher()` 在输入串上创建匹配器：

| 方法 | 语义 |
|------|------|
| `matches()` | **整串**必须完全匹配 |
| `find()` | 在串中查找**下一个**子串匹配 |
| `lookingAt()` | 从**开头**匹配，但不要求消耗整串 |

```java
Pattern phone = Pattern.compile("\\d{3}-\\d{4}");
Matcher m = phone.matcher("电话 123-4567 转 8");
if (m.find()) {
    System.out.println(m.group()); // 123-4567
}
```

Java 源码里反斜杠要写两个：`\\d` 表示正则 `\d`。

## 2. 字符类

| 写法 | 含义 |
|------|------|
| `\d` | 数字 `[0-9]` |
| `\w` | 单词字符 `[a-zA-Z0-9_]` |
| `\s` | 空白字符 |
| `\D \W \S` | 上述三者的取反 |
| `[a-f0-9]` | 自定义集合 |
| `[^0-9]` | 集合取反 |

```java
Pattern.compile("\\d+");     // 一段连续数字
Pattern.compile("[^,;]+");   // 非逗号分号的连续字符
```

## 3. 量词：贪婪 / 懒惰 / 独占

| 写法 | 含义 | 回溯 |
|------|------|------|
| `*` `+` `?` `{n,m}` | 贪婪：尽量多匹配 | 会回溯 |
| `*?` `+?` `??` `{n,m}?` | 懒惰：尽量少匹配 | 会回溯 |
| `*+` `++` `?+` `{n,m}+` | 独占：不回溯，宁可整体失败 | 不回溯 |

```java
"<.+>".matcher("<b>粗体</b>").find();   // 贪婪：<b>粗体</b>
"<.+?>".matcher("<b>粗体</b>").find();  // 懒惰：<b>
```

## 4. 锚点

- `^` 行/串开头，`$` 行/串结尾（配合 `MULTILINE` 变成每行边界）
- `\b` 单词边界：`\bcat\b` 不会命中 `catalog`

```java
Pattern.matches("^\\d+$", "12345"); // 整串是数字
```

## 5. 分组与替换

- 普通捕获组 `(...)`：用 `group(1)`、`group(2)` 取，编号按左括号出现顺序
- 命名组 `(?<name>...)`（Java 7+）：用 `group("name")` 取
- 非捕获组 `(?:...)`：只控制优先级，不占编号

```java
Matcher m = Pattern.compile("(?<y>\\d{4})-(?<m>\\d{2})-(?<d>\\d{2})")
        .matcher("2025-01-15");
if (m.matches()) {
    System.out.println(m.group("y")); // 2025
}

"2025-01-15".replaceAll("(\\d{4})-(\\d{2})-(\\d{2})", "$3/$2/$1"); // 15/01/2025
```

逐段拼装用 `appendReplacement` / `appendTail`；`replaceFirst` 只换第一处。

## 6. 匹配标志

| 标志 | 作用 |
|------|------|
| `CASE_INSENSITIVE` | 忽略大小写 |
| `MULTILINE` | `^` `$` 匹配每行行首/行尾 |
| `DOTALL` | `.` 也匹配换行 |
| `UNICODE_CHARACTER_CLASS` | `\w` 等按 Unicode 解释 |
| `LITERAL` | 整个模式当字面量 |

```java
Pattern.compile("^b", Pattern.MULTILINE);
Pattern.compile("(?i)hello"); // 内联标志
```

## 7. String.split 与 Pattern.split

`String.split(regex)` 便捷但每次重新编译；复杂场景用 `Pattern.split`，并注意第二个参数是**限制个数**：

```java
Pattern.compile(",").split("a,b,c");        // [a, b, c]
Pattern.compile(",").split("a,b,c", 2);     // [a, b,c]
```

## 8. 性能：预编译与回溯

`String.matches` / `replaceAll` / `split` 每次都会重新编译正则，高频路径应缓存 `Pattern`：

```java
private static final Pattern EMAIL = Pattern.compile("[\\w.+-]+@[\\w.-]+\\.[a-z]{2,}");
```

**灾难性回溯**：`(a+)+b` 这类“嵌套量词 + 可重叠”结构，失败时要尝试所有拆分方式，理论上 O(2ⁿ)。
经典攻击输入是一长串 `a` 且故意缺少结尾的 `b`。新版 OpenJDK 已缓解经典回溯爆炸，
但同一表达式在 JS / Python / 旧版 Java 上仍可能卡死，且更复杂的模式仍可能中招。

防御手段：

- 避免嵌套量词（`(...)+` 内部再出现 `*`/`+`）
- 用独占量词 `(a++)+b`：不回溯，失败立刻返回
- 对不可信输入限制长度再做正则

## 9. Java 9 / 11+ 便捷 API

- `Matcher.results()`（Java 9+）：所有匹配的 `Stream<MatchResult>`
- `Pattern.asMatchPredicate()` / `asPredicate()`（Java 11+）：直接当 `Predicate<String>` 用

```java
List<String> hit = words.stream()
        .filter(Pattern.compile("cat").asMatchPredicate())  // 整串匹配
        .toList();
```

## 10. 常用配方（注意局限）

```java
// 类邮箱：能挡明显错误，但不是 RFC 5322
"^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"

// 整数
"[-+]?\\d+"

// 日期 YYYY-MM-DD：只校验数字格式，2 月 31 日也会通过！
"^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$"
```

需要真实日历合法性时，请用 `LocalDate.parse` 而不是只靠正则。

## 11. 运行本模块

**VS Code**：打开 `src/com/learn/regex/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `PatternMatcher.java` | Pattern.compile / matches / find / lookingAt |
| `RegexSyntax.java` | 字符类、量词（贪婪/懒惰/独占）、锚点 |
| `GroupAndReplace.java` | 分组、命名组、替换与 appendReplacement |
| `RegexFlags.java` | CASE_INSENSITIVE / MULTILINE / DOTALL |
| `RegexPerformance.java` | 预编译 Pattern、灾难性回溯 |
| `RegexJava11Plus.java` | asMatchPredicate / asPredicate / results 与常用配方 |

## 易错点

1. `matches()` 是整串匹配，`find()` 才是子串查找，二者混用会导致“明明有却匹配不上”
2. Java 字符串里写正则要双反斜杠：`"\\d"` 而不是 `"\d"`（后者编译失败）
3. `String.replaceAll` 的第二个参数是**替换模板**，`$1`、`$` 有特殊含义，字面 `$` 要写 `$$`
4. 嵌套量词（如 `(a+)+`）遇到攻击性输入会灾难性回溯；别把不可信长文本直接丢给复杂正则
5. 高频路径不要用 `String.matches`——每次都重新编译，应缓存 `Pattern`
