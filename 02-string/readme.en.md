# Strings and Text

Strings are the most commonly used type in Java. This module covers the `String` constant pool and immutability, `StringBuilder`, formatting, and the modern text APIs introduced in Java 11–15 (text blocks, `strip`/`isBlank`/`lines`/`repeat`, and more). Under the hood: Java 9+ compact strings store purely Latin-1 strings in a `byte[]` to save memory; Java 18+ (JEP 400) standardized UTF-8 as the default charset.

## 1. String Basics

String literals go into the **string pool**, where identical contents share the same object; `new String("...")` always creates a new object on the heap:

```java
String a = "hello";
String b = "hello";           // same pooled object as a
String c = new String("hello"); // a new object on the heap
System.out.println(a == b);      // true, same object
System.out.println(a == c);      // false, different objects
System.out.println(a.equals(c)); // true, same contents
```

**Rule**: compare string contents with `equals` (or `equalsIgnoreCase`); `==` compares reference addresses. `intern()` manually places a string in the pool and returns the pooled reference.

Quick reference of commonly used methods:

| Method | Purpose |
|------|------|
| `length()` / `isEmpty()` | Length / whether the string is empty |
| `charAt(i)` | The character at index i |
| `indexOf("x")` / `lastIndexOf` | First / last occurrence; returns -1 if not found |
| `substring(1, 4)` | Substring `[1, 4)` |
| `replace("a", "b")` | Replaces all matches |
| `split(",")` | Splits by regex into a `String[]` |
| `contains` / `startsWith` / `endsWith` | Contains / prefix / suffix |
| `toUpperCase` / `toLowerCase` | Case conversion |

## 2. Immutability

`String` is an immutable class: every "modification" returns a **new object**, leaving the original string unchanged:

```java
String s = "hello";
String t = s.concat(" world");
System.out.println(s);       // hello — the original object is unchanged
System.out.println(s == t);  // false — t is a new object
```

Benefits of immutability: the constant pool can be shared safely, strings are inherently thread-safe, and `hashCode` can be cached (which makes strings great `HashMap` keys).

The cost: `s += "x"` in a loop produces many intermediate objects with O(n²) complexity; use `StringBuilder` for concatenation in loops.

## 3. StringBuilder and StringBuffer

These are mutable character sequences providing in-place edits such as `append` / `insert` / `delete` / `reverse`:

```java
StringBuilder sb = new StringBuilder("hello");
sb.append(" world").insert(0, ">> ").delete(3, 5).reverse();
String result = sb.toString();
```

| Type | Thread-safe | Performance | Typical use |
|------|----------|------|------|
| `StringBuilder` | No | High | Single-threaded concatenation (the default choice) |
| `StringBuffer` | Yes (methods are synchronized) | Slightly lower | Sharing one buffer across threads |

`StringBuilder` starts with a default capacity of 16; `ensureCapacity` / a constructor argument can reduce reallocation copies. `length()` is the character count, while `capacity()` is the capacity of the underlying array.

## 4. Text Blocks (Java 15+)

Write multi-line strings with `"""`; common indentation (incidental whitespace) is stripped automatically. Great for JSON / SQL / HTML:

```java
String json = """
        {
            "name": "Zhang San",
            "age": 18
        }
        """;
```

Details:

- The indentation of the closing `"""` determines how much of the "common prefix" is stripped; `stripIndent()` applies the same treatment to multi-line strings assembled at runtime
- A trailing `\` **merges the next line** (removes the newline)
- `\s` explicitly preserves a space (text blocks strip trailing whitespace by default)
- `"` usually needs no escaping inside a text block; `\"""` can represent three quote characters
- When the closing delimiter sits at the end of a content line, the string does not end with a newline

```java
String merged = """
        First line\
        Second line\
        end""";            // → "First lineSecond lineend"
String spaces = "A\sB\s";   // \s explicitly preserves a space
```

## 5. Formatting and join

`String.format` / `String.formatted` (Java 15+) build strings from a format string:

```java
String f = String.format("%s is %d years old, score %.2f", "Zhang San", 18, 95.5);
String g = "left-aligned[%-8s]|zero-padded[%05.2f]".formatted("hi", 3.14159);
```

Common specifiers: `%s` string, `%d` integer, `%f` floating point, `%x` hexadecimal, `%n` platform line separator, `%b` boolean.

`String.join` concatenates fragments with a delimiter (Java 8+):

```java
String joined = String.join(", ", "red", "green", "blue"); // red, green, blue
```

`translateEscapes()` (Java 15+) translates the "literal" escape sequences in a string into actual control characters.

## 6. Java 11+ Utility APIs

| API | Version | Purpose |
|-----|------|------|
| `strip()` / `stripLeading()` / `stripTrailing()` | Java 11 | Strips whitespace per `Character.isWhitespace` (includes ideographic spaces; U+00A0 does not count); more thorough than `trim` |
| `isBlank()` | Java 11 | Whether the string is empty or all whitespace |
| `lines()` | Java 11 | Splits into a `Stream<String>` by line |
| `repeat(n)` | Java 11 | Repeats n times |
| `indent(n)` | Java 12 | Indents the whole string (a negative value tries to remove indentation) |
| `transform(f)` | Java 12 | Transforms the string with a function |
| `stripIndent()` | Java 15 | Removes common indentation; **for strings ending with a newline, indentation stripping is skipped** |
| `translateEscapes()` | Java 15 | Translates escape sequences |

```java
System.out.println("  a  ".isBlank());     // false
System.out.println(" \t ".isBlank());      // true
System.out.println("ab".repeat(3));        // ababab
"red\ngreen".lines().forEach(System.out::println);
```

`trim()` only removes whitespace `<= U+0020`; for Unicode whitespace such as ideographic spaces use `strip()` (decided by `Character.isWhitespace`; U+00A0 non-breaking space is not among them).

## 7. Running This Module

**VS Code**: open `src/com/learn/str/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|------|
| `Main.java` | Main entry point; runs all examples in order |
| `StringBasics.java` | String pool, == vs equals, common methods |
| `StringImmutability.java` | Immutability and the loop-concatenation pitfall |
| `StringBuilderDemo.java` | StringBuilder / StringBuffer |
| `TextBlocks.java` | Text blocks, indentation, `\s`, `\"`, line merging |
| `StringFormat.java` | format / formatted / join |
| `StringJava11Plus.java` | New APIs such as strip / isBlank / lines / repeat |

## Common Pitfalls

1. `==` on two string variables compares references; content comparison must use `equals`
2. `s += piece` in a loop performs poorly; use `StringBuilder` instead
3. The end index of `substring` is **exclusive** — `substring(1, 4)` takes indices 1, 2, 3
4. The argument to `split` is a **regex**; to split on `.` write `split("\\.")`
5. A text block's indentation is determined by the position of the closing `"""`; misalign it and you get extra leading spaces
6. `String.formatted` is an instance method while `String.format` is static; don't mix up the two forms
7. `trim()` cannot remove ideographic spaces — use `strip()`. Neither, however, can remove a U+00A0 non-breaking space
8. For strings **ending with a newline**, `stripIndent()` only cleans up trailing whitespace and does not strip indentation — mind the ending in demos
