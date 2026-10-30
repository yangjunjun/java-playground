# Optional

`java.util.Optional` is a container object introduced in **Java 8** that makes "a value may be absent" explicit. It turns scattered `if (x != null)` checks at call sites into composable `map` / `filter` / `orElse` chains, reducing null-pointer errors and improving readability. It works best as a **method return type**, not as a field or parameter type.

## 1. Why Optional

The problem with `null`: callers easily forget the null check and one broken link in a chain triggers an NPE; the method signature also gives no hint that a value may be missing.

```java
// Traditional null style
String name = findUserNameOrNull(db, "2");
String upper = name == null ? "unknown" : name.toUpperCase();

// Optional style
String result = findUserName(db, "2")
        .map(String::toUpperCase)
        .orElse("unknown");
```

## 2. Creating an Optional

| Method | Behavior |
|--------|----------|
| `Optional.of(x)` | The value **must be non-null**; passing null throws NPE immediately |
| `Optional.ofNullable(x)` | Allows null; returns empty for null |
| `Optional.empty()` | Explicitly construct "no value" |

```java
Optional<String> present = Optional.of("hello");
Optional<String> maybe   = Optional.ofNullable(null); // empty
Optional<String> empty   = Optional.empty();
```

## 3. map / flatMap / filter

```java
optional.map(String::toUpperCase);   // transform if present
optional.filter(s -> s.length() > 2); // becomes empty if the predicate fails
optional.flatMap(User::getAddress);   // avoids nesting when the function returns Optional
```

**`map` vs. `flatMap`**: use `map` when the transform function returns a plain value; use `flatMap` when it returns an `Optional`, otherwise you end up with nested `Optional<Optional<...>>`.

## 4. Extracting Values and Fallbacks

| Method | Description |
|--------|-------------|
| `orElse(x)` | Returns the default when empty; **the argument is always evaluated** |
| `orElseGet(supplier)` | Calls the Supplier only when empty (lazy) |
| `orElseThrow()` | Throws `NoSuchElementException` when empty (no-arg version, **Java 10+**) |
| `orElseThrow(supplier)` | Throws the given exception when empty |
| `ifPresent(consumer)` | Consumes the value only when present |
| `ifPresentOrElse(c, r)` | **Java 9+**: run the consumer when present, the runnable when empty |
| `stream()` | **Java 9+**: becomes a stream of 0 or 1 elements |
| `isEmpty()` | **Java 11+**: whether there is no value |
| `isPresent()` | Whether a value is present |

```java
present.orElse("default");                          // the default is always computed
present.orElseGet(() -> expensiveDefault());     // lazy; computed only when empty
empty.ifPresentOrElse(v -> use(v), () -> fallback());
```

## 5. Common Usage Patterns

```java
// Chained transformation + fallback
Optional.of(raw).map(String::trim).filter(s -> !s.isEmpty()).map(String::toUpperCase).orElse("empty");

// Optional and Stream: flatten with Optional::stream (Java 9+)
opts.stream().flatMap(Optional::stream).collect(Collectors.toList());

// Collectors.flatMapping (Java 9+) handles nullable fields
Collectors.flatMapping(i -> Optional.ofNullable(i.tag).stream(), Collectors.toSet());

// orElseThrow for fail-fast
String must = Optional.ofNullable(env).orElseThrow();
```

## 6. Boundaries of Optional Usage (Important)

**Recommended**: use `Optional` only as a **method return type**, to express "the result of this call may be absent".

**Not recommended**:

1. **Do not** use `Optional` as a **field type** — it does not implement `Serializable`, has real memory overhead, and is poorly supported by frameworks (JPA, Jackson).
2. **Do not** use `Optional` as a **method parameter type** — it forces callers to wrap values and makes things more verbose, not less. This is community consensus and is also covered in the *Effective Java* discussion.
3. **Do not** wrap collection elements in `Optional` (e.g. `List<Optional<T>>`) — handle them with `filter`/`flatMap` in a stream, or just use `null`/a sentinel value.
4. **Do not** use `Optional` to express 0..N values — that is `List`/`Stream`'s job.

## 7. Running This Module

**VS Code**: open `src/com/learn/optional/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|----------|
| `Main.java` | Main entry point, runs all examples in order |
| `OptionalBasics.java` | Optional vs. null |
| `OptionalCreation.java` | of / ofNullable / empty |
| `OptionalMapFlatMap.java` | map / flatMap / filter |
| `OptionalOrElse.java` | orElse / orElseGet / orElseThrow / ifPresent |
| `OptionalPatterns.java` | Common patterns and Stream integration |
| `OptionalPitfalls.java` | Anti-patterns and boundaries |

## Common Pitfalls

1. Calling `get()` without `isPresent` / `orElse` — throws `NoSuchElementException` when empty
2. Using `orElse(expensive())` instead of `orElseGet(() -> expensive())` — the default is computed for nothing
3. Reaching for `map` when the transform function returns `Optional` — produces nested `Optional<Optional<T>>`; use `flatMap`
4. `Optional.of(null)` throws NPE immediately; use `ofNullable` to accept null
5. Using `Optional` as a field/parameter type — framework serialization fails and the API gets more verbose
6. The no-arg `orElseThrow()` is **Java 10+**; `ifPresentOrElse` is **Java 9+**; `isEmpty` is **Java 11+**; `stream()` is **Java 9+** — they will not compile on older JDKs
7. Wrapping collection elements in `Optional` (`List<Optional<T>>`) — flatten with `flatMap(Optional::stream)` in a stream instead
8. Assuming `Optional` can eliminate `null` entirely — fields, arrays, and collections can still contain null
