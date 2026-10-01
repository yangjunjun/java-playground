# 集合框架（Collections）

集合框架是 Java 存放与操作成组对象的标准 API，位于 `java.util` 包。它把常用数据结构（动态数组、链表、哈希表、红黑树、堆、队列）统一成一套接口：`Collection` 管单列元素，`Map` 管键值对。理解集合的接口层次、实现类取舍与 `equals/hashCode` 契约，是写出高效、正确代码的基础。

## 1. 集合体系结构

| 根接口 | 子接口 | 常用实现 |
|--------|--------|----------|
| `Collection` | `List` | `ArrayList`、`LinkedList`、`Vector`（遗留） |
| `Collection` | `Set` | `HashSet`、`LinkedHashSet`、`TreeSet` |
| `Collection` | `Queue` | `PriorityQueue`、`ArrayDeque`、`LinkedList` |
| `Map`（不继承 Collection） | `SortedMap`/`NavigableMap` | `HashMap`、`LinkedHashMap`、`TreeMap`、`Hashtable`（遗留） |

```java
Collection<String> list = new ArrayList<>(List.of("a", "b", "c")); // 有序、可重复
Collection<String> set  = new HashSet<>(List.of("a", "b", "a"));   // 去重
Map<String, Integer> map = new HashMap<>();                        // 键值对
```

## 2. List：ArrayList 与 LinkedList

| 对比项 | `ArrayList` | `LinkedList` |
|--------|-------------|--------------|
| 底层 | 数组 | 双向链表 |
| 随机访问 `get(i)` | O(1) | O(n) |
| 尾部追加 | 均摊 O(1) | O(1) |
| 中间插入/删除 | 要搬移元素 | 改指针（但定位要 O(n)） |
| 适用 | 绝大多数场景 | 频繁头尾操作、当队列/栈用 |

`Vector` 是遗留类，方法带 `synchronized`，性能差，新代码不要用。

```java
List<String> list = new ArrayList<>();
list.add("Java");
list.add(1, "Go");          // 中间插入
LinkedList<String> linked = new LinkedList<>(list);
linked.addFirst("C");
linked.addLast("Rust");
```

## 3. 遍历与 fail-fast

ArrayList/HashMap 等的迭代器是 **fail-fast** 的：遍历中直接调集合的 `add`/`remove` 做结构性修改，会抛 `ConcurrentModificationException`。

```java
// 错误：for-each 里直接删除
for (String s : list) {
    if ("b".equals(s)) {
        list.remove(s); // 抛 ConcurrentModificationException
    }
}

// 正确一：用迭代器自己的 remove
for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
    if ("c".equals(it.next())) {
        it.remove();
    }
}

// 正确二：Java 8+ 的 removeIf
list.removeIf(s -> s.equals("d"));
```

需要“边遍历边修改”时，可用 `CopyOnWriteArrayList`（见第 8 节）或 `java.util.concurrent` 中的并发集合。

## 4. Set 与 equals/hashCode 契约

- `HashSet`：无序，基于 `HashMap`，允许一个 `null`。
- `LinkedHashSet`：保持插入顺序。
- `TreeSet`：按自然序或 `Comparator` 排序，基于红黑树，不允许 `null`（自然序时）。

`HashSet`/`HashMap` 判等依赖契约：**`equals` 相等则 `hashCode` 必须相等**；两者必须一起重写。

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Point)) return false;
    Point p = (Point) o;
    return x == p.x && y == p.y;
}

@Override
public int hashCode() {
    return Objects.hash(x, y);
}
```

只重写 `equals` 不重写 `hashCode` 时，两个“相等”的对象可能落在不同桶里，`contains`/`get` 会失效。

## 5. Map 家族

- `HashMap`：允许一个 `null` 键与多个 `null` 值，不保证顺序。默认初始容量 16、负载因子 0.75；元素数超过 `capacity * loadFactor` 就扩容为 2 倍。**Java 8+**：链表长度 ≥ 8 且容量 ≥ 64 时树化为红黑树，查找由 O(n) 降为 O(log n)。
- `LinkedHashMap`：保持插入顺序；构造传 `accessOrder = true` 时按访问顺序，可用来实现 LRU。
- `TreeMap`：按键排序，实现 `NavigableMap`，提供 `floorEntry`、`headMap` 等导航方法。
- `Hashtable`：遗留同步 Map，不允许 `null` 键/值，新代码别用。
- `ConcurrentHashMap`：并发安全，不允许 `null` 键/值。

遍历推荐 `entrySet`，比 `keySet` + `get` 少一次查找：

```java
for (Map.Entry<String, Integer> e : scores.entrySet()) {
    System.out.println(e.getKey() + " => " + e.getValue());
}
```

## 6. Queue / Deque

`PriorityQueue` 基于堆，出队顺序按优先级（自然序或 `Comparator`），`peek`/`poll` 取的是堆顶而非入队顺序。`ArrayDeque` 是双端队列，头尾操作 O(1)，比 `LinkedList` 更快，可当队列或栈使用。

```java
Queue<Integer> pq = new PriorityQueue<>(List.of(5, 1, 3));
Deque<String> stack = new ArrayDeque<>();
stack.push("一");
stack.push("二");
System.out.println(stack.pop()); // 二
```

## 7. Collections 工具类

`java.util.Collections` 提供静态工具：`sort`、`binarySearch`（**必须先有序**）、`frequency`、`max`/`min`、`shuffle`、`synchronizedList`、`unmodifiableList`。

```java
Collections.sort(numbers);
int idx = Collections.binarySearch(numbers, 5);
List<Integer> view = Collections.unmodifiableList(src); // 是“视图”，随底层变化
```

`Collections.unmodifiableList` 返回的是**视图**：通过原引用修改底层集合，视图也会变。要真正的不可变副本请用 `List.copyOf` / `List.of`（见下节）。

## 8. 不可变工厂与 Collectors（Java 9/10+）

`List.of`、`Set.of`、`Map.of`、`Map.copyOf`（**Java 9+**）返回真不可变集合：拒绝 `null`；`Set.of`/`Map.of` 还拒绝重复元素。

```java
List<String> list = List.of("a", "b");
Map<String, Integer> map = Map.of("one", 1);
Map<String, Integer> copy = Map.copyOf(map); // Java 10+
```

Stream 的 `Collectors.toList()` / `toSet()` / `toMap()` 可把流收集成可变集合，`Collectors.toUnmodifiableList()`（**Java 10+**）收集成不可变集合。`Stream.toList()` 见 stream 模块（**Java 16+**）。

## 9. SequencedCollections（Java 21+）

**Java 21+** 引入 `SequencedCollection` / `SequencedSet` / `SequencedMap`，统一了有两端顺序的集合 API：`getFirst` / `getLast` / `reversed` / `addFirst` / `addLast`，`SequencedMap` 还有 `firstEntry` / `lastEntry`。

```java
List<String> list = new ArrayList<>(List.of("a", "b", "c"));
list.getFirst();          // a
list.getLast();           // c
list.reversed();          // [c, b, a]

SequencedMap<String, Integer> map = new LinkedHashMap<>();
map.put("one", 1);
map.firstEntry();         // one=1
```

## 10. 并发集合（简介）

`CopyOnWriteArrayList` 写时复制，迭代器遍历旧快照，遍历中修改也不会抛 `ConcurrentModificationException`，适合读多写少。`ConcurrentHashMap` 并发安全（**Java 8+** CAS + `synchronized`），提供 `merge`、`computeIfAbsent` 等原子操作。完整并发编程见 concurrency 模块。

## 11. 运行本模块

**VS Code**：打开 `src/com/learn/collections/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `CollectionHierarchy.java` | Collection / Map 体系结构 |
| `ListDemo.java` | ArrayList、LinkedList、Vector 与 fail-fast |
| `SetDemo.java` | HashSet / LinkedHashSet / TreeSet 与 equals/hashCode 契约 |
| `MapDemo.java` | HashMap、LinkedHashMap、TreeMap、Hashtable、ConcurrentHashMap |
| `QueueDeque.java` | PriorityQueue、ArrayDeque 与栈用法 |
| `CollectionsUtil.java` | Collections 排序、查找、同步包装、不可修改视图 |
| `SequencedCollections.java` | Java 21+ 的 SequencedCollection/Set/Map |
| `ConcurrentCollectionsBrief.java` | 并发集合简介与指向 |

## 易错点

1. 遍历中直接调用 `list.remove`/`map.put` 做结构性修改，抛 `ConcurrentModificationException`——用 `Iterator.remove` 或 `removeIf`
2. 只重写 `equals` 不重写 `hashCode`，`HashSet`/`HashMap` 的 `contains`/`get` 失效
3. `Collections.binarySearch` 前忘记排序，结果未定义
4. `Collections.unmodifiableList` 是视图不是副本，底层改动会透过视图暴露
5. `List.of` 允许重复，但 `Set.of`/`Map.of` 遇重复键会抛 `IllegalArgumentException`；它们都不接受 `null`
6. `HashMap` 允许 `null` 键/值，`Hashtable` 与 `ConcurrentHashMap` 都不允许——用错会 NPE
7. `PriorityQueue` 的 `peek` 取的是优先级最高的元素，不是先进先出
8. 单线程场景误用 `Vector`/`Hashtable`/`synchronizedList`，白付同步开销
