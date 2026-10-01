# 日期时间（java.time）

Java 8 引入了全新的 `java.time` API（JSR-310），彻底取代了老旧的 `java.util.Date` / `Calendar`。新 API 不可变、线程安全、API 语义清晰（日期、时间、带时区时刻分开表达），是现代 Java 日期时间编程的唯一推荐选择。本模块覆盖从 Java 8 到 Java 25 的常用能力。

## 1. LocalDate / LocalTime / LocalDateTime（Java 8+）

三者都不带时区，分别表示「日期」「时间」「日期+时间」，全部**不可变**：

```java
LocalDate today = LocalDate.now();
LocalDate birthday = LocalDate.of(2000, 5, 20);
LocalTime time = LocalTime.of(8, 30);
LocalDateTime dt = LocalDateTime.of(2024, 1, 1, 12, 0);

// plus / minus 做加减，with 做替换，都返回新对象
LocalDate nextWeek = today.plusWeeks(1);
LocalDate lastYear = today.minusYears(1);
LocalDate firstOfMonth = today.withDayOfMonth(1);
```

常用取值：`getYear()`、`getMonthValue()`、`getDayOfMonth()`、`getDayOfWeek()`、`getDayOfYear()`。

## 2. Instant 与时钟 Clock（Java 8+）

`Instant` 是时间线上的一个点（UTC 时间戳），适合机器计算与跨时区存储：

```java
Instant now = Instant.now();          // 例如 2024-06-01T03:15:30.123456Z
long epochMilli = now.toEpochMilli(); // 自 1970-01-01Z 的毫秒数
```

`Clock` 把「取当前时间」抽象出来，方便测试时注入固定时钟：

```java
Clock clock = Clock.systemUTC();
Clock fixed = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
Instant fixedNow = fixed.instant();   // 永远返回 2024-01-01T00:00:00Z
```

## 3. Duration / Period（Java 8+）

两者都表示「一段时间差」：

| 类型 | 语义 | 示例 |
|------|------|------|
| `Duration` | 基于时间（秒/纳秒） | 2 小时 30 分钟 |
| `Period` | 基于日期（年/月/日） | 2 年 3 个月 4 天 |

```java
Duration d = Duration.ofHours(2).plusMinutes(30);
Period p = Period.of(2, 3, 4);
LocalDateTime a = LocalDateTime.of(2024, 1, 1, 0, 0);
LocalDateTime b = a.plus(d);
Duration between = Duration.between(a, b);
```

## 4. ZonedDateTime / ZoneId / ZoneOffset（Java 8+）

`ZonedDateTime` = 本地日期时间 + 时区规则（夏令时等）：

```java
ZoneId shanghai = ZoneId.of("Asia/Shanghai");
ZonedDateTime zdt = ZonedDateTime.now(shanghai);
ZonedDateTime ny = zdt.withZoneSameInstant(ZoneId.of("America/New_York"));
```

`ZoneOffset` 是固定偏移量（如 `+08:00`），`ZoneId` 还包含夏令时规则。**优先用 `ZoneId` 而不是 `TimeZone`**：`TimeZone` 属于旧 API，偏移量计算依赖默认时区、可变、易踩坑；`ZoneId` 是不可变的，且与 `java.time` 全家无缝配合。

## 5. DateTimeFormatter：格式化与解析（Java 8+）

```java
DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
String text = LocalDateTime.now().format(fmt);
LocalDateTime parsed = LocalDateTime.parse("2024-01-01 08:30:00", fmt);
```

还有内置的 ISO 格式与本地化格式：

```java
String iso = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
DateTimeFormatter cn = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
        .withLocale(Locale.CHINA);
System.out.println(LocalDate.now().format(cn));
```

解析失败会抛 `DateTimeParseException`；`ofPattern` 写错模式会抛 `IllegalArgumentException`。

## 6. truncatedTo：截断精度（Java 8+）

把时间截断到指定单位：

```java
LocalTime t = LocalTime.of(14, 30, 45, 123_456_789);
LocalTime minute = t.truncatedTo(ChronoUnit.MINUTES); // 14:30
```

## 7. 旧 API 对比与互转

`java.util.Date` / `Calendar` 的问题：可变（非线程安全）、月份从 0 开始、时区混用 `LocalDate` 语义。新旧互转：

```java
Date date = new Date();
Instant instant = date.toInstant();               // Date -> Instant
Date back = Date.from(instant);                    // Instant -> Date

// LocalDate <-> Date/Instant（需要时区参与）
LocalDate ld = LocalDate.now();
Date fromDate = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
LocalDate fromDate2 = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
```

## 8. 常用工具方法

```java
// 两日期之间相差的天数
long days = ChronoUnit.DAYS.between(LocalDate.of(2024, 1, 1), LocalDate.now());

// 某月第一天 / 最后一天
LocalDate first = LocalDate.now().withDayOfMonth(1);
LocalDate last = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());

// 判断闰年
boolean leap = Year.of(2024).isLeap();
```

合法性检查：`LocalDate.of(2024, 2, 30)` 会直接抛 `DateTimeException`，可用 try/catch 判断输入是否合法。

## 9. 运行本模块

**VS Code**：打开 `src/com/learn/datetime/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `LocalDateBasic.java` | LocalDate/LocalTime/LocalDateTime 创建与增减改查 |
| `LocalTimeAndDateTime.java` | LocalTime 与 LocalDateTime 常用操作 |
| `ZonedAndDuration.java` | ZonedDateTime/ZoneId 与 Duration/Period |
| `InstantAndClock.java` | Instant 时间戳与 Clock 抽象时钟 |
| `DateTimeFormat.java` | DateTimeFormatter 格式化与解析 |
| `DateLegacy.java` | 旧 Date/Calendar 问题与新旧互转 |
| `DateTimeUtils.java` | between、firstDayOfMonth 等常用工具 |

## 易错点

1. 旧 `java.util.Date` 的月份从 0 开始（`getMonth()` 返回 0 表示一月），`java.time` 从 1 开始
2. `Date`/`SimpleDateFormat` 可变且非线程安全；`java.time` 全部不可变，天然线程安全
3. 用 `ofPattern("yyyy-MM-dd")` 解析时，解析成 `LocalDateTime` 会缺时间部分而失败——类型要与模式匹配
4. `Period.of(1, 15, 0)` 不会自动进位成 1 年 3 个月，显示时要注意；算差值优先用 `ChronoUnit`
5. 涉及夏令时的时区转换要用 `withZoneSameInstant`，不要用 `withZoneSameLocal` 后简单加减小时
6. 存储/传输统一用 `Instant` 或带时区的 `ZonedDateTime`，不要用裸 `LocalDateTime` 当“绝对时刻”
