# Modern Syntax

This module collects the most commonly used language features from Java 9 through 25 that boost everyday coding productivity: type inference, switch expressions, text blocks, records, sealed classes, pattern matching, and more. Each section gives a "before / after" comparison to help you migrate old-style code to modern style.

## 1. var Local Variable Type Inference (Java 10+)

`var` lets the compiler infer the local variable's type from the initializer expression; the bytecode is still strongly typed — this is **not dynamic typing**:

```java
var list = new ArrayList<String>();   // inferred as ArrayList<String>
var map = Map.of("k", 1);            // inferred as Map<String, Integer>
```

**Limitations (must-know)**:

| Limitation | Example |
|------|------|
| Local variables only | Field `var field = 1;` fails to compile |
| Cannot be a method return type | `var method() {...}` fails to compile |
| Must have an initializer | `var x;` fails to compile |
| Cannot be initialized with `null` alone | `var n = null;` fails to compile (type unknown) |
| Not usable for lambda parameters | `var fn = (x) -> x + 1;` fails to compile |

Best used when "the type is obvious from the right-hand side"; overuse hurts readability.

## 2. switch Expressions (Java 14+)

Upgraded from statement to expression, with arrow syntax and `yield`, and no `break` needed:

```java
// old: statement form, easy to forget break and fall through
String kind;
switch (day) {
    case SATURDAY: case SUNDAY: kind = "weekend"; break;
    default: kind = "weekday";
}

// new: expression + arrow
String kind = switch (day) {
    case SATURDAY, SUNDAY -> "weekend";
    case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "weekday";
};

// multi-line branches return with yield
int code = switch (day) {
    case MONDAY -> { System.out.println("Monday"); yield 1; }
    default    -> 0;
};
```

As an expression it must cover all cases; with a fully covered enum you can omit `default`.

## 3. Text Blocks (Java 15+)

Wrap multi-line text in triple quotes `"""`; indentation and newlines are handled automatically — ideal for JSON / SQL / HTML:

```java
String json = """
        {
          "name": "John",
          "age": 18
        }
        """;
```

Escaping essentials:

- `\\` produces a single backslash (essential for Windows paths)
- `\s` preserves a space (trailing spaces are stripped by default)
- The position of the closing `"""` determines whether the string ends with a newline

## 4. Record Classes (Java 16+)

Immutable data carriers that auto-generate `equals` / `hashCode` / `toString` / accessors:

```java
record Point(int x, int y) {
    Point {                       // compact constructor: just validation
        if (x < 0) throw new IllegalArgumentException("x must not be negative");
    }
    static Point origin() { return new Point(0, 0); }  // static factory
}

interface Shape { double area(); }
record Circle(double r) implements Shape {
    public double area() { return Math.PI * r * r; }
}
```

Key points:

- Accessors are `x()` / `y()`, **not** `getX()`
- Can implement interfaces, use compact constructors, and add static factories
- **Not a replacement for mutable JavaBeans** (no setters, final fields)

## 5. Sealed Classes (Java 17+)

Use `permits` to control exactly which classes can extend or implement:

```java
sealed interface Shape permits Circle, Rectangle, Triangle {}
record Circle(double r) implements Shape {}
record Rectangle(double w, double h) implements Shape {}
final record Triangle(double a, double b, double c) implements Shape {}  // final
```

Choose one of three modifiers: `final` (cannot be extended further), `sealed` (stays sealed), `non-sealed` (opened up again).
Combined with switch pattern matching you get **exhaustiveness checking** — adding a subclass that isn't covered becomes a compile error.

## 6. Pattern Matching (Java 16 / 21+)

**instanceof patterns (Java 16+)** — test and cast in one step:

```java
if (obj instanceof String s) {     // done in one step
    System.out.println(s.length());
}
```

**switch pattern matching (Java 21+)** — cases bind types directly:

```java
String desc = switch (obj) {
    case String s           -> "string: " + s;
    case Integer i          -> "integer: " + i;
    case Point(int x, int y) -> "point: " + x + "," + y;   // record deconstruction
    case null               -> "null";
    default                 -> "other";
};
```

**Record deconstruction (Java 21+)**: `case Point(int x, int y) ->` unpacks components directly, and can nest.

**Guard clauses with when (Java 21+)**: `case String s when s.length() > 2 ->` adds a condition inside the case.
When the guard fails, matching falls through to later cases instead of throwing.

**Note**: guards like `case String s when ...` are **non-exhaustive**; you still need `default` or full type coverage.

## 7. Unnamed Patterns and Variables (Java 21+ / standard in 22+)

Use `_` to mean "intentionally ignored":

```java
if (o instanceof Point(int x, int _)) {}   // ignore y
if (o instanceof Point _) {}               // bind nothing at all
switch (obj) {
    case String _ -> "is a string";        // don't care about the content
}
try { ... } catch (ArithmeticException _) {}  // don't bind the exception either
```

More honest and clearer than declaring unused variables.

## 8. Flexible Statements in Constructors (Java 22+)

You can write ordinary statements before `super()` / `this()`:

```java
User(String raw) {
    String trimmed = raw.trim();          // allowed in Java 22+
    if (trimmed.isEmpty()) throw new IllegalArgumentException("name must not be empty");
    super(trimmed.toLowerCase());
}
```

The old style forced you to shove validation into a static method and pass its result to `super()`.

## 9. More Cutting-Edge Features (Java 23 ~ 25, for awareness only)

| Feature | Version | Notes |
|------|------|------|
| Primitive type pattern matching `case int i ->` | Java 25 **preview** | Requires `--enable-preview`; not enabled in this module's code |
| Module import declaration `import module java.base;` | Java 25 | Write it directly; no more long import lists |
| Compact source files and instance main | Java 25 | Write `void main()` in a single file, no class needed |
| Simplified main, `IO.println` | Java 25 | `java.lang.IO` provides `IO.println` |

Module import example (Java 25+):

```java
import module java.base;   // import all exported packages of java.base at once

void main() {
    IO.println(List.of("a", "b"));
}
```

## Running This Module

**VS Code**: open `src/com/learn/modern/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|------|
| `Main.java` | Main entry point; runs all examples in sequence |
| `VarAndTypeInference.java` | var type inference and its limitations |
| `SwitchExpressions.java` | switch expressions, arrow syntax, yield |
| `TextBlocks.java` | Text blocks, `\\` and `\s` escapes |
| `Records.java` | Record compact constructors, accessors, static factories |
| `SealedClasses.java` | sealed permits, exhaustive switch |
| `PatternMatching.java` | instanceof / switch / record deconstruction / when |
| `UnnamedPatterns.java` | Unnamed patterns `_`, unnamed variables |
| `FlexibleCtorBrief.java` | Statements before super() in Java 22+ constructors |

## Common Pitfalls

1. `var` is not dynamic typing: the type is fixed at compile time, and it can't be used on fields or return types
2. `var x = null;` fails to compile — null has no inferable type
3. A switch expression missing `default` or not covering every enum constant → compile error (this is a feature, not a bug)
4. Record accessors are `x()`, not `getX()` — an easy trap when using JavaBean-style reflection
5. You can't set fields on a record; don't use one as a mutable DTO
6. A new subclass missing from a sealed `permits` list → compile error (actually a good thing: it forces you to maintain the contract)
7. When a `case String s when ...` guard fails, matching moves to later cases — it does not throw
8. Once you declare an unnamed variable `_`, you can no longer use that name
9. Text blocks strip trailing spaces by default; use `\s` to keep them
