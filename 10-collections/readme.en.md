# Collections Framework

The Collections Framework is Java's standard API for storing and manipulating groups of objects, living in the `java.util` package. It unifies the common data structures (dynamic arrays, linked lists, hash tables, red-black trees, heaps, queues) into one set of interfaces: `Collection` handles single elements, `Map` handles key-value pairs. Understanding the interface hierarchy, choosing the right implementation, and honoring the `equals`/`hashCode` contract are the foundations of writing efficient, correct code.

## 1. Collection Architecture

| Root Interface | Subinterface | Common Implementations |
|--------|--------|----------|
| `Collection` | `List` | `ArrayList`, `LinkedList`, `Vector` (legacy) |
| `Collection` | `Set` | `HashSet`, `LinkedHashSet`, `TreeSet` |
| `Collection` | `Queue` | `PriorityQueue`, `ArrayDeque`, `LinkedList` |
| `Map` (does not extend Collection) | `SortedMap`/`NavigableMap` | `HashMap`, `LinkedHashMap`, `TreeMap`, `Hashtable` (legacy) |

```java
Collection<String> list = new ArrayList<>(List.of("a", "b", "c")); // ordered, allows duplicates
Collection<String> set  = new HashSet<>(List.of("a", "b", "a"));   // no duplicates
Map<String, Integer> map = new HashMap<>();                        // key-value pairs
```

## 2. List: ArrayList vs LinkedList

| Aspect | `ArrayList` | `LinkedList` |
|--------|-------------|--------------|
| Backing store | Array | Doubly linked list |
| Random access `get(i)` | O(1) | O(n) |
| Append at end | Amortized O(1) | O(1) |
| Insert/remove in middle | Must shift elements | Rewires pointers (but locating the position is O(n)) |
| Fits | The vast majority of cases | Frequent head/tail operations, used as a queue/stack |

`Vector` is a legacy class whose methods are `synchronized`; it performs poorly and should not be used in new code.

```java
List<String> list = new ArrayList<>();
list.add("Java");
list.add(1, "Go");          // insert in the middle
LinkedList<String> linked = new LinkedList<>(list);
linked.addFirst("C");
linked.addLast("Rust");
```

## 3. Iteration and fail-fast

The iterators of ArrayList/HashMap and friends are **fail-fast**: making a structural modification via the collection's own `add`/`remove` during iteration throws `ConcurrentModificationException`.

```java
// Wrong: removing directly in a for-each
for (String s : list) {
    if ("b".equals(s)) {
        list.remove(s); // throws ConcurrentModificationException
    }
}

// Right, option 1: use the iterator's own remove
for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
    if ("c".equals(it.next())) {
        it.remove();
    }
}

// Right, option 2: removeIf (Java 8+)
list.removeIf(s -> s.equals("d"));
```

When you need to modify while iterating, use `CopyOnWriteArrayList` (see Section 8) or the concurrent collections in `java.util.concurrent`.

## 4. Set and the equals/hashCode Contract

- `HashSet`: unordered, backed by `HashMap`, allows one `null`.
- `LinkedHashSet`: preserves insertion order.
- `TreeSet`: sorted by natural order or a `Comparator`, backed by a red-black tree, disallows `null` (when using natural order).

Equality in `HashSet`/`HashMap` relies on this contract: **if `equals` is true, `hashCode` must be equal**; the two must always be overridden together.

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

If you override `equals` without `hashCode`, two "equal" objects may land in different buckets and `contains`/`get` will fail.

## 5. The Map Family

- `HashMap`: allows one `null` key and multiple `null` values, with no order guarantee. Default initial capacity 16, load factor 0.75; when the element count exceeds `capacity * loadFactor`, it doubles in size. **Java 8+**: when a bin's linked list reaches length ≥ 8 and the capacity is ≥ 64, the bin is treeified into a red-black tree, bringing lookup from O(n) down to O(log n).
- `LinkedHashMap`: preserves insertion order; when constructed with `accessOrder = true` it orders by access, which can be used to implement an LRU cache.
- `TreeMap`: sorted by key, implements `NavigableMap`, and provides navigation methods such as `floorEntry` and `headMap`.
- `Hashtable`: legacy synchronized Map; disallows `null` keys/values; don't use it in new code.
- `ConcurrentHashMap`: concurrency-safe; disallows `null` keys/values.

For iteration, prefer `entrySet`, which saves one lookup compared to `keySet` + `get`:

```java
for (Map.Entry<String, Integer> e : scores.entrySet()) {
    System.out.println(e.getKey() + " => " + e.getValue());
}
```

## 6. Queue / Deque

`PriorityQueue` is heap-based; dequeue order follows priority (natural order or a `Comparator`), so `peek`/`poll` return the heap top rather than insertion order. `ArrayDeque` is a double-ended queue with O(1) head/tail operations, faster than `LinkedList`, usable as a queue or a stack.

```java
Queue<Integer> pq = new PriorityQueue<>(List.of(5, 1, 3));
Deque<String> stack = new ArrayDeque<>();
stack.push("one");
stack.push("two");
System.out.println(stack.pop()); // two
```

## 7. The Collections Utility Class

`java.util.Collections` provides static utilities: `sort`, `binarySearch` (**the list must already be sorted**), `frequency`, `max`/`min`, `shuffle`, `synchronizedList`, `unmodifiableList`.

```java
Collections.sort(numbers);
int idx = Collections.binarySearch(numbers, 5);
List<Integer> view = Collections.unmodifiableList(src); // it is a "view" — it tracks the underlying collection
```

`Collections.unmodifiableList` returns a **view**: if you modify the underlying collection through the original reference, the view reflects the change. For a true immutable copy, use `List.copyOf` / `List.of` (see the next section).

## 8. Immutable Factories and Collectors (Java 9/10+)

`List.of`, `Set.of`, `Map.of`, and `Map.copyOf` (**Java 9+**) return truly immutable collections: they reject `null`; `Set.of`/`Map.of` also reject duplicate elements.

```java
List<String> list = List.of("a", "b");
Map<String, Integer> map = Map.of("one", 1);
Map<String, Integer> copy = Map.copyOf(map); // Java 10+
```

The Stream collectors `Collectors.toList()` / `toSet()` / `toMap()` gather a stream into mutable collections, while `Collectors.toUnmodifiableList()` (**Java 10+**) gathers into an immutable one. See the stream module for `Stream.toList()` (**Java 16+**).

## 9. Sequenced Collections (Java 21+)

**Java 21+** introduces `SequencedCollection` / `SequencedSet` / `SequencedMap`, unifying the API for collections with a defined order at both ends: `getFirst` / `getLast` / `reversed` / `addFirst` / `addLast`, plus `firstEntry` / `lastEntry` on `SequencedMap`.

```java
List<String> list = new ArrayList<>(List.of("a", "b", "c"));
list.getFirst();          // a
list.getLast();           // c
list.reversed();          // [c, b, a]

SequencedMap<String, Integer> map = new LinkedHashMap<>();
map.put("one", 1);
map.firstEntry();         // one=1
```

## 10. Concurrent Collections (Brief Overview)

`CopyOnWriteArrayList` copies on write; its iterators traverse an old snapshot, so modifying during iteration never throws `ConcurrentModificationException` — a good fit for read-heavy, write-light workloads. `ConcurrentHashMap` is concurrency-safe (**Java 8+**, CAS + `synchronized`) and offers atomic operations such as `merge` and `computeIfAbsent`. See the concurrency module for full concurrent programming.

## 11. Running This Module

**VS Code**: open `src/com/learn/collections/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|------|
| `Main.java` | Main entry point; runs all examples in order |
| `CollectionHierarchy.java` | The Collection / Map architecture |
| `ListDemo.java` | ArrayList, LinkedList, Vector, and fail-fast |
| `SetDemo.java` | HashSet / LinkedHashSet / TreeSet and the equals/hashCode contract |
| `MapDemo.java` | HashMap, LinkedHashMap, TreeMap, Hashtable, ConcurrentHashMap |
| `QueueDeque.java` | PriorityQueue, ArrayDeque, and stack usage |
| `CollectionsUtil.java` | Collections sorting, searching, synchronized wrappers, unmodifiable views |
| `SequencedCollections.java` | SequencedCollection/Set/Map in Java 21+ |
| `ConcurrentCollectionsBrief.java` | Brief intro to concurrent collections and where to go next |

## Common Pitfalls

1. Making structural changes via `list.remove`/`map.put` during iteration throws `ConcurrentModificationException` — use `Iterator.remove` or `removeIf`
2. Overriding `equals` but not `hashCode` breaks `contains`/`get` on `HashSet`/`HashMap`
3. Forgetting to sort before `Collections.binarySearch` gives undefined results
4. `Collections.unmodifiableList` is a view, not a copy — changes to the underlying collection show through
5. `List.of` allows duplicates, but `Set.of`/`Map.of` throw `IllegalArgumentException` on duplicates; none of them accept `null`
6. `HashMap` allows `null` keys/values while `Hashtable` and `ConcurrentHashMap` do not — using the wrong one gives an NPE
7. `PriorityQueue`'s `peek` returns the highest-priority element, not the first one in
8. Misusing `Vector`/`Hashtable`/`synchronizedList` in single-threaded code pays the synchronization cost for nothing
