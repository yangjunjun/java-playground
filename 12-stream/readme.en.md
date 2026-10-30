# Stream API

The Stream API is the declarative data-processing model introduced in **Java 8**: it separates *what* to do (`filter`/`map`/`collect`) from *how* to do it (loops, parallelism). A stream is not a collection — it holds no data. It is a one-shot computation pipeline over a data source (collection, array, generator function), and with lambdas it yields concise, parallelizable aggregation code.

## 1. Streams vs. Collections; Lazy Evaluation

- A **collection** is a data container, concerned with *storing and retrieving*; a **stream** is a description of a computation, concerned with *computing and collecting*.
- **Lazy**: intermediate operations (`filter`/`map`/...) do not run until a terminal operation (`collect`/`forEach`/...) triggers them.
- **One-shot**: once consumed, reusing a stream throws `IllegalStateException`; call `stream()` again to recompute.

```java
List<String> result = words.stream()
        .peek(s -> System.out.println("entering pipeline: " + s)) // lazy; runs only when collect triggers
        .filter(s -> s.length() > 5)
        .map(String::toUpperCase)
        .collect(Collectors.toList());
```

## 2. Creating Streams

```java
Stream.of("a", "b", "c");                  // explicit elements
list.stream();                             // Collection
Arrays.stream(new int[]{1, 2, 3});         // array
Stream.generate(() -> "tick").limit(3);    // infinite stream; must be truncated
Stream.iterate(1, n -> n * 2).limit(5);    // infinite stream
Stream.concat(Stream.of("x"), Stream.of("y"));
IntStream.range(1, 5);                     // [1, 5)
IntStream.rangeClosed(1, 5);               // [1, 5]
Stream.<String>builder().add("p").build();
```

## 3. Intermediate Operations (Return a Stream, Chainable)

| Operation | Effect |
|-----------|--------|
| `map` | One-to-one transformation |
| `filter` | Filter by predicate |
| `flatMap` | One-to-many, flattens nested structures |
| `distinct` | Remove duplicates (by `equals`) |
| `sorted` | Sort (natural order or `Comparator`) |
| `limit` / `skip` | Truncate / skip |
| `peek` | Side-channel view (for debugging; avoid side effects) |

```java
List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3, 4));
nested.stream().flatMap(Collection::stream).collect(Collectors.toList()); // [1,2,3,4]
```

## 4. Terminal Operations

- Traversal: `forEach` (no ordering guarantee) and `forEachOrdered` (preserves order even in parallel).
- Aggregation: `count`, `reduce`, `mapToInt(...).summaryStatistics()`.
- Matching: `anyMatch` / `allMatch` / `noneMatch` (**short-circuiting**, stop on first hit).
- Lookup: `findFirst` / `findAny` / `min` / `max`.

```java
int sum = numbers.stream().reduce(0, Integer::sum);
IntSummaryStatistics stats = numbers.stream().mapToInt(Integer::intValue).summaryStatistics();
boolean any = numbers.stream().anyMatch(n -> n > 8); // short-circuits
```

## 5. Collectors

```java
Collectors.toList() / toSet() / toUnmodifiableList() // Java 10+
Collectors.joining(", ", "[", "]")
Collectors.toMap(keyFn, valueFn)                     // duplicate keys require a mergeFunction
Collectors.groupingBy(Employee::dept)                // grouping
Collectors.groupingBy(dept, Collectors.counting())   // group and aggregate
Collectors.partitioningBy(s -> s.length() > 3)       // partition into true/false
Collectors.summingInt(e -> e.salary)
Collectors.teeing(counting(), summingInt(...), (c, t) -> ...) // Java 12+
```

`Map.Entry` can be sorted and traversed directly:

```java
map.entrySet().stream().sorted(Map.Entry.comparingByKey())...
```

## 6. Parallel Streams

`parallelStream()` / `parallel()` parallelize through the `ForkJoinPool`. **Suitable for**: large data sets, stateless, side-effect-free work. **Dangerous**: shared mutable state (e.g. `forEach(list::add)`) loses data or throws; `forEach` does not guarantee order.

```java
long sum = numbers.parallelStream().mapToLong(Integer::longValue).sum(); // safe
// numbers.parallelStream().forEach(shared::add); // unsafe
```

## 7. Stream.toList() (Java 16+)

`Stream.toList()` (**Java 16+**) returns an **unmodifiable** `List` and does not allow `null` elements. `Collectors.toList()` returns a mutable `ArrayList`. Prefer `Stream.toList()` for read-only scenarios; use `Collectors.toList()` when you need to modify the result. `Collectors.toUnmodifiableList()` (**Java 10+**) is likewise immutable.

```java
List<String> list = Stream.of("a", "b").toList(); // unmodifiable
```

There is also `mapMulti` (**Java 16+**, an alternative to `flatMap` that creates fewer intermediate objects) and `Stream.ofNullable` (**Java 9+**).

## 8. Primitive Streams

`IntStream` / `LongStream` / `DoubleStream` avoid autoboxing and offer `sum` / `average` / `max` / `summaryStatistics` directly. `boxed()` converts back to a boxed stream; `mapToObj` converts to an object stream.

```java
IntStream.rangeClosed(1, 3).mapToObj(i -> "No. " + i).collect(Collectors.toList());
DoubleStream.of(1.5, 2.5).summaryStatistics();
```

## 9. Running This Module

**VS Code**: open `src/com/learn/stream/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|----------|
| `Main.java` | Main entry point, runs all examples in order |
| `StreamBasics.java` | Differences from collections, lazy evaluation, one-shot nature |
| `StreamCreation.java` | of / stream / generate / iterate / concat / range |
| `IntermediateOps.java` | map / filter / flatMap / distinct / sorted / limit / skip |
| `TerminalOps.java` | forEach / reduce / statistics / short-circuit matching and lookup |
| `CollectorsDemo.java` | toList, joining, groupingBy, partitioningBy, teeing |
| `ParallelStreams.java` | When parallel streams fit and thread-safety hazards |
| `StreamJava16Plus.java` | Immutability of `Stream.toList()` and mapMulti |
| `PrimitiveStreams.java` | IntStream / LongStream / DoubleStream |

## Common Pitfalls

1. Treating a stream like a collection: streams hold no data and cannot be traversed repeatedly — after one use they throw `IllegalStateException`
2. Forgetting `limit` on infinite streams (`generate` / `iterate`) — the terminal operation runs forever
3. Mutating a collection inside a stream, or using `peek`/`map` with side effects — results are nondeterministic under parallelism
4. Sharing mutable state in parallel streams (e.g. `forEach(list::add)`) — lost updates or exceptions
5. `Collectors.toMap` throws `IllegalStateException` on duplicate keys — supply a `mergeFunction`
6. Assuming `Stream.toList()` returns a mutable list — it is unmodifiable and mutating it throws `UnsupportedOperationException`
7. Reaching for `forEach` when order matters — parallel streams do not guarantee order; use `forEachOrdered`
8. Parallelizing small data sets — thread scheduling overhead outweighs any gain
