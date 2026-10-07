# Stream API（Stream）

Stream API 是 **Java 8** 引入的声明式数据处理模型：把“要做什么”（`filter`/`map`/`collect`）与“怎么做”（循环、并行）分离。流不是集合，它不存数据，而是对数据源（集合、数组、生成函数）的一次性计算流水线，配合 Lambda 可写出简洁、可并行的聚合代码。

## 1. Stream 与集合的区别、惰性求值

- **集合**是数据容器，关注“存与取”；**流**是计算描述，关注“算与收”。
- **惰性**：中间操作（`filter`/`map`/...）在终止操作（`collect`/`forEach`/...）触发前不执行。
- **一次性**：流消费一次后再调用会抛 `IllegalStateException`，需要再算就重新 `stream()`。

```java
List<String> result = words.stream()
        .peek(s -> System.out.println("进入流水线: " + s)) // 惰性，collect 时才执行
        .filter(s -> s.length() > 5)
        .map(String::toUpperCase)
        .collect(Collectors.toList());
```

## 2. Stream 的创建

```java
Stream.of("a", "b", "c");                  // 显式元素
list.stream();                             // Collection
Arrays.stream(new int[]{1, 2, 3});         // 数组
Stream.generate(() -> "tick").limit(3);    // 无限流，必须截断
Stream.iterate(1, n -> n * 2).limit(5);    // 无限流
Stream.concat(Stream.of("x"), Stream.of("y"));
IntStream.range(1, 5);                     // [1, 5)
IntStream.rangeClosed(1, 5);               // [1, 5]
Stream.<String>builder().add("p").build();
```

## 3. 中间操作（返回 Stream，可链式）

| 操作 | 作用 |
|------|------|
| `map` | 一对一转换 |
| `filter` | 按谓词过滤 |
| `flatMap` | 一对多并摊平嵌套结构 |
| `distinct` | 去重（按 `equals`） |
| `sorted` | 排序（自然序或 `Comparator`） |
| `limit` / `skip` | 截取 / 跳过 |
| `peek` | 旁路查看（调试用，应避免副作用） |

```java
List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3, 4));
nested.stream().flatMap(Collection::stream).collect(Collectors.toList()); // [1,2,3,4]
```

## 4. 终止操作

- 遍历：`forEach`（不保序）与 `forEachOrdered`（并行时保序）。
- 聚合：`count`、`reduce`、`mapToInt(...).summaryStatistics()`。
- 匹配：`anyMatch` / `allMatch` / `noneMatch`（**短路**，命中即停）。
- 查找：`findFirst` / `findAny` / `min` / `max`。

```java
int sum = numbers.stream().reduce(0, Integer::sum);
IntSummaryStatistics stats = numbers.stream().mapToInt(Integer::intValue).summaryStatistics();
boolean any = numbers.stream().anyMatch(n -> n > 8); // 短路
```

## 5. Collectors 收集器

```java
Collectors.toList() / toSet() / toUnmodifiableList() // Java 10+
Collectors.joining(", ", "[", "]")
Collectors.toMap(keyFn, valueFn)                     // 重复键需给 mergeFunction
Collectors.groupingBy(Employee::dept)                // 分组
Collectors.groupingBy(dept, Collectors.counting())   // 分组聚合
Collectors.partitioningBy(s -> s.length() > 3)       // 分区成 true/false
Collectors.summingInt(e -> e.salary)
Collectors.teeing(counting(), summingInt(...), (c, t) -> ...) // Java 12+
```

`Map.Entry` 可直接排序与遍历：

```java
map.entrySet().stream().sorted(Map.Entry.comparingByKey())...
```

## 6. 并行流

`parallelStream()` / `parallel()` 借助 `ForkJoinPool` 并行。**适用**：大数据量、无状态、无副作用。**危险**：共享可变状态（如 `forEach(list::add)`）会丢数据或抛异常；`forEach` 不保证顺序。

```java
long sum = numbers.parallelStream().mapToLong(Integer::longValue).sum(); // 安全
// numbers.parallelStream().forEach(shared::add); // 不安全
```

## 7. Stream.toList()（Java 16+）

`Stream.toList()`（**Java 16+**）返回**不可修改**的 `List`，不允许 `null` 元素。`Collectors.toList()` 返回可变的 `ArrayList`。只读场景推荐 `Stream.toList()`；要改结果就用 `Collectors.toList()`。`Collectors.toUnmodifiableList()`（**Java 10+**）同样不可变。

```java
List<String> list = Stream.of("a", "b").toList(); // 不可修改
```

另外还有 `mapMulti`（**Java 16+**，`flatMap` 的替代，减少中间对象）与 `Stream.ofNullable`（**Java 9+**）。

## 8. 原始类型流

`IntStream` / `LongStream` / `DoubleStream` 避免自动装箱，直接提供 `sum` / `average` / `max` / `summaryStatistics`。`boxed()` 转回装箱流，`mapToObj` 转对象流。

```java
IntStream.rangeClosed(1, 3).mapToObj(i -> "第" + i + "名").collect(Collectors.toList());
DoubleStream.of(1.5, 2.5).summaryStatistics();
```

## 9. 运行本模块

**VS Code**：打开 `src/com/learn/stream/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `StreamBasics.java` | 与集合的区别、惰性求值、一次性 |
| `StreamCreation.java` | of / stream / generate / iterate / concat / range |
| `IntermediateOps.java` | map / filter / flatMap / distinct / sorted / limit / skip |
| `TerminalOps.java` | forEach / reduce / 统计 / 短路匹配与查找 |
| `CollectorsDemo.java` | toList、joining、groupingBy、partitioningBy、teeing |
| `ParallelStreams.java` | 并行流适用场景与线程安全隐患 |
| `StreamJava16Plus.java` | `Stream.toList()` 不可变性与 mapMulti |
| `PrimitiveStreams.java` | IntStream / LongStream / DoubleStream |

## 易错点

1. 把 Stream 当集合用：流不存数据、不能反复遍历，消费一次后抛 `IllegalStateException`
2. 无限流（`generate` / `iterate`）忘记 `limit`，终止操作会一直跑下去
3. 在流中修改集合或做有副作用的 `peek`/`map`，并行时结果不确定
4. 并行流里共享可变状态（如 `forEach(list::add)`），丢更新或抛异常
5. `Collectors.toMap` 遇重复键抛 `IllegalStateException`，需给 `mergeFunction`
6. 以为 `Stream.toList()` 返回可变列表——它是不可修改的，改它抛 `UnsupportedOperationException`
7. 需要顺序时误用 `forEach`，并行流下顺序不保证，应改 `forEachOrdered`
8. 对小数据量开并行流，线程调度开销反而得不偿失
