# Arrays

Arrays are the only container built into the language: fixed-length, able to hold primitives or reference types, with random access via index. This module covers declaration and initialization, copying, multidimensional arrays, the `java.util.Arrays` utility class, sorting, and binary search. For collection containers such as `ArrayList`, see the collections module.

## 1. Array Basics

Declaration and initialization:

```java
int[] a = new int[5];          // declared first, then filled; defaults to all 0
int[] b = { 1, 2, 3 };         // static initialization
int[] c = new int[] { 1, 2 };  // anonymous array
String[] names = new String[3]; // reference arrays default to all null
```

`int[] a` and `int a[]` are equivalent; the former is preferred. Once created, an array's **length cannot change**, and `length` is a field (not a method):

```java
System.out.println(b.length);        // 3
System.out.println(b[0]);            // indices start at 0
// b[3] is out of bounds → ArrayIndexOutOfBoundsException
```

Element default values match those of fields: `0` for integers, `0.0` for floating point, `'\u0000'` for `char`, `false` for `boolean`, and `null` for references. Traverse with for-each or a classic for loop (use the latter when you need the index).

### Array Covariance and ArrayStoreException

Arrays are **covariant**: a `String[]` can be assigned to an `Object[]`. But the runtime still checks against the actual element type:

```java
Object[] arr = new String[2];
arr[0] = "ok";
arr[1] = 100;   // compiles fine, throws ArrayStoreException at runtime
```

## 2. Copying Arrays

Three approaches; note that for reference arrays all of them are **shallow copies** (they copy references, not objects):

```java
int[] src = { 1, 2, 3, 4, 5 };
int[] c1 = src.clone();                             // copies the whole array
int[] c2 = Arrays.copyOf(src, 3);                   // {1,2,3}; can grow or truncate
int[] c3 = new int[3];
System.arraycopy(src, 1, c3, 0, 3);                 // copies 3 elements from src[1] into c3[0]
```

| Approach | Characteristics |
|------|------|
| `clone()` | Built-in array method; copies the entire array |
| `System.arraycopy` | Native method, fastest; lets you specify the range and destination position |
| `Arrays.copyOf` / `copyOfRange` | Backed by arraycopy; also lets you change the length along the way |

## 3. Multidimensional Arrays

A two-dimensional array is an "array of arrays"; **rows may have different lengths** (jagged arrays):

```java
int[][] m = new int[2][3];      // 2 rows by 3 columns, all 0
int[][] jagged = {
        { 1 },
        { 1, 2 },
        { 1, 2, 3 }
};
System.out.println(jagged.length);      // 3 rows
System.out.println(jagged[1].length);   // row 2 has 2 elements
```

`m[i][j]` accesses row i, column j; `new int[2][]` creates the row array first, then builds the columns row by row. Three or more dimensions work the same way but are rare in business code — usually the data is modeled with objects or a Map instead.

## 4. The java.util.Arrays Utility Class

| Method | Purpose |
|------|------|
| `toString(a)` | Readable output of a one-dimensional array: `[1, 2, 3]` |
| `deepToString(a)` | Recursive output for multidimensional arrays |
| `fill(a, v)` / `fill(a, from, to, v)` | Fill the whole array / a range |
| `copyOf` / `copyOfRange` | Copy / extract a range |
| `equals(a, b)` / `deepEquals` | Element-by-element equality (not reference comparison) |
| `setAll(a, i -> expr)` | Fill by computing from the index (Java 8+) |
| `compare(a, b)` | Lexicographic comparison (Java 9+); returns negative/zero/positive |
| `mismatch(a, b)` | Index of the first differing element (Java 9+) |

```java
int[] arr = new int[3];
Arrays.setAll(arr, i -> i * 10);        // [0, 10, 20]
System.out.println(Arrays.toString(arr));
```

`List.of(...)` produces an **immutable List** (Java 9+); trade-offs versus arrays:

```java
List<String> list = List.of("a", "b");  // immutable; out-of-bounds access throws
String[] arr = { "a", "b" };            // fixed length but elements are mutable; can hold primitives
```

## 5. Sorting and Searching

```java
int[] a = { 3, 1, 2 };
Arrays.sort(a);                 // primitives: dual-pivot quicksort, in-place
Arrays.parallelSort(a);         // parallel sort for large arrays (Java 8+)
int idx = Arrays.binarySearch(a, 2); // the array must already be sorted!
```

Key points:

- `sort` uses dual-pivot quicksort for primitive arrays and TimSort for object arrays (objects must implement `Comparable`, or you must pass a `Comparator`)
- `binarySearch` requires the array to be sorted in ascending order, otherwise the result is undefined; if not found it returns `-(insertion point) - 1`
- Sorting an object array only moves references; the objects themselves are not copied
- `Integer[]` and `int[]` are not interchangeable: sorting and `Arrays.equals` behave according to the element type

## 6. Running This Module

**VS Code**: open `src/com/learn/array/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|------|
| `Main.java` | Main entry point; runs all examples in order |
| `ArrayBasics.java` | Declaration, default values, length, the covariance trap |
| `ArrayCopy.java` | clone / arraycopy / copyOf |
| `MultiDimArray.java` | Two-dimensional and jagged arrays |
| `ArraysUtil.java` | Common Arrays utilities and comparison with List.of |
| `ArraySortSearch.java` | sort / parallelSort / binarySearch |

## Common Pitfalls

1. Array length is the `length` field; only strings have the `length()` method
2. An array's length is fixed after creation; to grow it you must create a new array and copy
3. `Object[] arr = new String[1]; arr[0] = 1;` compiles but throws `ArrayStoreException` at runtime
4. Results of `binarySearch` on an unsorted array are meaningless
5. `Arrays.equals` compares contents, `==` compares references — don't mix them up
6. `int[]` and `Integer[]` cannot be assigned to each other; `List<int[]>` holds array objects
7. `clone()` is a shallow copy for reference arrays; after copying, both arrays share the element objects
