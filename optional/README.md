# Optional

`java.util.Optional` 是 **Java 8** 引入的容器对象，用于**明确表达“一个值可能不存在”**。它把 `null` 检查从调用方的散落 `if (x != null)` 变成可组合的 `map` / `filter` / `orElse` 链式调用，减少空指针、提升可读性。它最适合作为**方法返回类型**，而不是字段或参数类型。

## 1. 为什么需要 Optional

`null` 的问题：调用方容易忘记判空，链式调用一点就 NPE；方法签名也看不出“可能没值”。

```java
// 传统 null 写法
String name = findUserNameOrNull(db, "2");
String upper = name == null ? "未知" : name.toUpperCase();

// Optional 写法
String result = findUserName(db, "2")
        .map(String::toUpperCase)
        .orElse("未知");
```

## 2. 创建 Optional

| 方法 | 行为 |
|------|------|
| `Optional.of(x)` | 值**必须非 null**，传 null 立刻 NPE |
| `Optional.ofNullable(x)` | 允许 null，null 时返回 empty |
| `Optional.empty()` | 显式构造“无值” |

```java
Optional<String> present = Optional.of("hello");
Optional<String> maybe   = Optional.ofNullable(null); // empty
Optional<String> empty   = Optional.empty();
```

## 3. map / flatMap / filter

```java
optional.map(String::toUpperCase);   // 有值则变换
optional.filter(s -> s.length() > 2); // 不满足谓词则变 empty
optional.flatMap(User::getAddress);   // 函数返回 Optional 时避免嵌套
```

**`map` 与 `flatMap` 的区别**：变换函数返回普通值用 `map`；返回 `Optional` 用 `flatMap`，否则会得到 `Optional<Optional<...>>` 嵌套。

## 4. 取值与兜底

| 方法 | 说明 |
|------|------|
| `orElse(x)` | 无值时返回默认值；**参数总是被求值** |
| `orElseGet(supplier)` | 无值时才调用 Supplier（惰性） |
| `orElseThrow()` | 无值抛 `NoSuchElementException`（**Java 10+** 无参版） |
| `orElseThrow(supplier)` | 无值抛指定异常 |
| `ifPresent(consumer)` | 有值才消费 |
| `ifPresentOrElse(c, r)` | **Java 9+**：有值走 consumer，无值走 runnable |
| `stream()` | **Java 9+**：变成 0/1 元素的流 |
| `isEmpty()` | **Java 11+**：是否无值 |
| `isPresent()` | 是否有值 |

```java
present.orElse("默认");                          // 默认值总是被计算
present.orElseGet(() -> expensiveDefault());     // 惰性，无值才计算
empty.ifPresentOrElse(v -> use(v), () -> fallback());
```

## 5. 常见使用模式

```java
// 链式转换 + 兜底
Optional.of(raw).map(String::trim).filter(s -> !s.isEmpty()).map(String::toUpperCase).orElse("空");

// Optional 与 Stream：Optional::stream 摊平（Java 9+）
opts.stream().flatMap(Optional::stream).collect(Collectors.toList());

// Collectors.flatMapping（Java 9+）处理可空字段
Collectors.flatMapping(i -> Optional.ofNullable(i.tag).stream(), Collectors.toSet());

// orElseThrow 提前失败
String must = Optional.ofNullable(env).orElseThrow();
```

## 6. Optional 使用边界（重要）

**推荐**：`Optional` 只做**方法返回类型**，表达“调用结果可能无值”。

**不推荐**：

1. **不要**把 `Optional` 当**字段类型** —— 它不实现 `Serializable`，内存开销大，框架（JPA、Jackson）支持差。
2. **不要**把 `Optional` 当**方法参数类型** —— 强迫调用方包一层，反而更啰嗦。这是社区共识，也写进了《Effective Java》的讨论中。
3. **不要**在集合里装 `Optional`（如 `List<Optional<T>>`）—— 用 `filter`/`flatMap` 在流里处理，或直接用 `null`/哨兵值。
4. **不要**用 `Optional` 表达 0..N 个值 —— 那是 `List`/`Stream` 的职责。

## 7. 运行本模块

**VS Code**：打开 `src/com/learn/optional/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `OptionalBasics.java` | Optional 与 null 的对比 |
| `OptionalCreation.java` | of / ofNullable / empty |
| `OptionalMapFlatMap.java` | map / flatMap / filter |
| `OptionalOrElse.java` | orElse / orElseGet / orElseThrow / ifPresent |
| `OptionalPatterns.java` | 常见模式与 Stream 集成 |
| `OptionalPitfalls.java` | 反模式与边界 |

## 易错点

1. 不加 `isPresent` / `orElse` 直接 `get()`，empty 时抛 `NoSuchElementException`
2. 用 `orElse(expensive())` 而不是 `orElseGet(() -> expensive())`，默认值白白计算
3. 变换函数返回 `Optional` 时误用 `map`，产生 `Optional<Optional<T>>` 嵌套，应改 `flatMap`
4. `Optional.of(null)` 直接 NPE；要接受 null 请用 `ofNullable`
5. 把 `Optional` 当字段/参数类型，框架序列化失败、API 变啰嗦
6. `orElseThrow()` 无参版是 **Java 10+**；`ifPresentOrElse` 是 **Java 9+**；`isEmpty` 是 **Java 11+**；`stream()` 是 **Java 9+**，在旧 JDK 上会编译失败
7. 用 `Optional` 包裹集合元素（`List<Optional<T>>`），不如在流里 `flatMap(Optional::stream)` 摊平
8. 以为 `Optional` 能完全消灭 null —— 字段、数组、集合内部仍可能出现 null
