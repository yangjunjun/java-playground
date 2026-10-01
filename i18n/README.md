# 国际化（i18n）

国际化（Internationalization，缩写 i18n）让同一套代码适配不同语言与地区的用户。Java 的核心工具：`Locale`（地区）、`ResourceBundle`（资源束）、`MessageFormat`（消息模板）、`NumberFormat` / `DateTimeFormatter`（数字与日期本地化）、`Collator`（排序）。

## 1. Locale

`Locale` = language（小写）+ country（大写）+ variant；也可用 BCP 47 语言标签：

```java
Locale zhCn = Locale.of("zh", "CN");          // Locale.of（Java 19+）
// 旧写法: new Locale("zh", "CN")  // Java 25 中已标记 @Deprecated
Locale enUs = Locale.US;
Locale hans = Locale.forLanguageTag("zh-Hans-CN");  // Java 7+

Locale.getDefault();          // 操作系统默认
Locale.setDefault(Locale.US); // 切换默认（影响不带 Locale 参数的工厂方法）
```

**业务代码应始终显式传 `Locale`，不要依赖默认值。**

## 2. ResourceBundle

资源束按命名规则回退：`BaseName_language_COUNTRY.properties` → `BaseName_language.properties` → `BaseName.properties`。

本模块的资源文件在 `src/i18n/`：

| 文件 | 覆盖范围 |
|------|----------|
| `Messages_zh_CN.properties` | 简体中文-中国 |
| `Messages_en_US.properties` | 英语-美国 |
| `Messages.properties` | 兜底默认 |

```java
ResourceBundle zh = ResourceBundle.getBundle("i18n.Messages", Locale.CHINA);
System.out.println(zh.getString("app.title"));
```

`run.sh` 用 `java -cp out:src` 让 `src/i18n/Messages*.properties` 进入 classpath。
Java 9+ 的 `.properties` 资源束默认按 UTF-8 读取（JEP 226），中文可直接写。

## 3. MessageFormat

占位符 `{0}`、`{1}`，可嵌入数字/日期格式：

```java
MessageFormat.format("你好，{0}！", "小明");

// 复合格式
new MessageFormat("订单 {0}：{1,number,currency}，时间 {2,date,short}")
        .format(new Object[]{ 1001, 199.5, new Date() });
```

`ChoiceFormat` 处理复数/量词分支：

```java
"{0,choice,0#没有文件|1#只有一个文件|1<共有 {0,number,integer} 个文件}"
```

**坑 1**：`MessageFormat` 里单引号是转义符，字面 `'` 要写 `''`。
**坑 2**：子格式（`number,currency` / `date`）的 Locale 在**构造时**就固化；
`setLocale` 之后必须重新 `applyPattern`，否则货币会退化成通用符号 `¤`。

## 4. NumberFormat

```java
NumberFormat.getCurrencyInstance(Locale.CHINA).format(1234567.89); // ￥1,234,567.89
NumberFormat.getPercentInstance(Locale.US).format(0.8567);         // 85.67%
NumberFormat.getIntegerInstance(Locale.JAPAN).format(1234567890);  // 1,234,567,890
```

金额计算用 `BigDecimal`，不要用 `double` 做钱。

## 5. 日期时间本地化

```java
DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale.CHINA);
DateTimeFormatter.ofPattern("yyyy年MM月dd日 EEEE", Locale.CHINA);

ZonedDateTime shanghai = dateTime.atZone(ZoneId.of("Asia/Shanghai"));
shanghai.withZoneSameInstant(ZoneId.of("UTC")); // 换时区展示
```

存储/传输用 ISO-8601，展示时再按用户 `Locale` 格式化。

## 6. Collator 排序

`String.compareTo` 只看 UTF-16 码点，对中文毫无意义：

```java
List<String> cities = List.of("上海", "北京", "广州");
cities.sorted(Collator.getInstance(Locale.CHINA)); // 按拼音/笔画规则
```

`Collator.PRIMARY / SECONDARY / TERTIARY / IDENTICAL` 控制比较强度（是否忽略大小写/重音）。

## 7. 运行本模块

**VS Code**：打开 `src/com/learn/i18n/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

`run.sh` 使用 `java -cp out:src`，这样 `src/i18n/*.properties` 才能被 `ResourceBundle` 找到。

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `src/i18n/Messages*.properties` | zh_CN / en_US / 兜底资源束 |
| `LocaleDemo.java` | Locale 语言/国家/脚本，默认值切换 |
| `ResourceBundleDemo.java` | 资源束加载与回退链 |
| `MessageFormatDemo.java` | 占位符、ChoiceFormat |
| `NumberFormatDemo.java` | 货币 / 百分比 / 整数 |
| `DateFormatI18n.java` | DateTimeFormatter.ofLocalized* 与时区 |
| `CollatorBrief.java` | 本地化排序与比较强度 |

## 易错点

1. 资源文件命名写错（`Messages_zh-CN.properties`）——国家代码必须下划线分隔且大写：`Messages_zh_CN`
2. `ResourceBundle` baseName 用斜杠 `i18n/Messages`——必须用点分隔 `i18n.Messages`
3. 忘记把资源目录放进 classpath → 运行时 `MissingResourceException`
4. `MessageFormat` 模板里单引号没转义 → 输出丢引号或占位符失效
5. 用 `String.compareTo` 排中文 → 码点顺序不是用户期望的拼音/笔画序
6. 硬编码日期格式 `yyyy/MM/dd` 而不用 `Locale` → 美国用户看到歧义日期
7. 用默认 `Locale` 做持久化格式化 → 服务器环境一变数据就解析失败
