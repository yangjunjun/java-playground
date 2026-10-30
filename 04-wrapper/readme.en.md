# Wrapper Classes and Autoboxing

Java provides a wrapper class for each of the 8 primitive types (`Integer`, `Double`, …), letting primitives participate in generics, collections, and reflection, and letting you express "no value" (`null`). This module covers wrapper constants, autoboxing/unboxing, the Integer cache trap, parsing utilities, and comparison approaches. The examples target Java 8+.

## 1. Wrapper Class Basics

| Primitive | Wrapper | Notes |
|----------|--------|------|
| `byte` | `Byte` | |
| `short` | `Short` | |
| `int` | `Integer` | The most inconsistent name of the bunch |
| `long` | `Long` | |
| `float` | `Float` | |
| `double` | `Double` | |
| `char` | `Character` | |
| `boolean` | `Boolean` | |

Wrapper classes are immutable objects that provide constants and utility methods:

```java
System.out.println(Integer.MAX_VALUE);  // 2147483647
System.out.println(Integer.MIN_VALUE);  // -2147483648
System.out.println(Integer.SIZE);       // 32 (bits)
System.out.println(Integer.BYTES);      // 4
System.out.println(Integer.TYPE);       // int.class (the Class object of the primitive)
```

Other commonly used constants include `Double.MAX_VALUE`, `Double.NaN`, `Character.MIN_VALUE`, and so on. `Integer.TYPE` differs from `Integer.class`: the former is the Class of `int`, the latter the Class of the wrapper.

## 2. Autoboxing and Unboxing (Java 5+)

The compiler converts between primitives and wrappers automatically — pure syntactic sugar:

```java
Integer i = 100;   // autoboxing → Integer.valueOf(100)
int n = i;         // unboxing → i.intValue()

List<Integer> list = new ArrayList<>();
list.add(1);       // boxed on the way in
int first = list.get(0); // unboxed on the way out
```

Notes:

- **Unboxing `null` throws `NullPointerException`**: wrapper types can be null, so check before unboxing
- Boxing and unboxing have object-creation overhead; prefer primitives in hot loops
- Assigning an `Integer` to a `long` first unboxes, then widens

## 3. The Integer Cache Trap

`Integer.valueOf` returns cached objects (`IntegerCache`) for values in **-128 ~ 127**, and only creates new ones outside that range:

```java
Integer a = 127;
Integer b = 127;
Integer c = 128;
Integer d = 128;
System.out.println(a == b);   // true, the same cached object
System.out.println(c == d);   // false, two new objects
System.out.println(c.equals(d)); // true
```

`Byte`/`Short`/`Integer`/`Long` all cache -128~127, `Character` caches 0~127, and `Boolean` has only two constants. **Always use `equals` to compare wrapper types** — `==` compares references only.

`new Integer(...)` is deprecated (`@Deprecated` since Java 9); use `Integer.valueOf` or autoboxing instead.

## 4. Parsing and Conversion

```java
int n = Integer.parseInt("42");          // returns int; most common
int n2 = Integer.parseInt("1010", 2);    // parsed as binary → 10
Integer obj = Integer.valueOf("42");     // returns Integer (hits the cache)
int n3 = Integer.decode("0x1F");         // supports 0x / 0 / # prefixes → 31
double d = Double.parseDouble("3.14");
boolean flag = Boolean.parseBoolean("true");
```

Failed parsing throws `NumberFormatException`. Converting back:

```java
String s = Integer.toString(255);
String hex = Integer.toHexString(255);   // ff
String bin = Integer.toBinaryString(10); // 1010
```

## 5. Comparison and Choosing Between Them

```java
Integer a = 200, b = 200;
System.out.println(a == b);             // false (outside the cache range)
System.out.println(a.equals(b));        // true
System.out.println(Integer.compare(a, b)); // 0
System.out.println(a.compareTo(b));     // 0
```

Every wrapper class has a static `compare(x, y)` method and an instance `compareTo` method. For `Float`/`Double`, the sort position of `NaN` is consistent with `Collections.sort` (`NaN` is treated as the largest value).

**When to use wrappers, when to use primitives**:

| Scenario | Choice |
|------|------|
| Local variables, arithmetic, loop variables | Primitives (no object overhead) |
| Generic parameters, collection elements | Wrappers (`List<Integer>`; `List<int>` is not allowed) |
| When you need to express "no value" | Wrappers (`null`) |
| Reflection, serialized fields where absence must be distinguishable | Wrappers |

Database entity fields commonly use wrappers precisely to distinguish "not filled in" (`null`) from "value is 0".

## 6. Running This Module

**VS Code**: open `src/com/learn/wrapper/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|------|
| `Main.java` | Main entry point; runs all examples in order |
| `WrapperBasics.java` | The 8 wrapper classes and common constants |
| `Autoboxing.java` | Autoboxing/unboxing and the NPE from unboxing null |
| `CachePitfall.java` | The Integer cache and the == trap |
| `ParseUtils.java` | parseInt / valueOf / decode |
| `WrapperCompare.java` | equals / compare / compareTo and selection advice |

## Common Pitfalls

1. `Integer a = 128; Integer b = 128; a == b` is `false` — outside the cache range these are two separate objects
2. Use `equals` to compare `Integer` values; `==` is only coincidentally true within the cache range and cannot be relied upon
3. Unboxing `null` throws `NullPointerException`; when pulling wrappers from a database or Map, check for null first
4. `Integer.parseInt` returns `int` while `valueOf` returns `Integer` — don't confuse them
5. `new Integer(...)` is deprecated; use `Integer.valueOf` or autoboxing
6. `List<int>` fails to compile; generic parameters must be wrappers: `List<Integer>`
7. Avoid `==` for `Double` comparison: for floating-point precision issues use a tolerance range or `Double.compare`
