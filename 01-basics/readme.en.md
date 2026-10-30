# Language Basics

This module covers the building blocks of Java syntax: variables and primitive types, operators, control flow and loops, and input/output. Whether you are writing business code or reading framework source, these rules apply everywhere. The examples target Java 8+; features beyond that are annotated with the version that introduced them.

## 1. Variables and Primitive Types

Java has 8 primitive types:

| Type | Bits | Notes |
|------|------|------|
| `byte` | 8 | -128 ~ 127 |
| `short` | 16 | -32768 ~ 32767 |
| `int` | 32 | Roughly ±2.1 billion; **default type for integer literals** |
| `long` | 64 | Larger integers; suffix literals with `L` |
| `float` | 32 | Single-precision floating point; suffix literals with `F` |
| `double` | 64 | Double-precision floating point; **default type for decimal literals** |
| `char` | 16 | A single Unicode character, e.g. `'A'`, `'中'` |
| `boolean` | — | Only `true` / `false`; cannot be used as 0/1 |

Literal syntax (binary literals and underscores are Java 7+):

```java
int binary = 0b1010;      // binary
int octal  = 017;         // octal, prefix 0
int hex    = 0xFF;        // hexadecimal, prefix 0x
int n      = 1_000_000;   // underscores only aid readability; they disappear at compile time
long big   = 3_000_000_000L;  // values beyond int range must be suffixed with L
float f    = 3.14F;           // without F this would be treated as double
char c     = '\u4E2D';        // Unicode escape for "中"
```

**Default values**: fields (member variables) have default values — `0` for integers, `0.0` for floating point, `'\u0000'` for `char`, `false` for `boolean`, and `null` for reference types. **Local variables have no default value**; using one before assignment is a compile-time error.

### Type Conversion

Widening conversions happen automatically and are safe: `byte → short → int → long → float → double`; `char` can widen to `int`.

```java
int i = 10;
long l = i;          // automatic widening
double d = 3.99;
int x = (int) d;     // forced narrowing: truncates the fraction → 3 (not rounding)
byte b = (byte) 130; // overflow wraps around in two's complement → -126
```

### Local Variable Type Inference with var (Java 10+)

```java
var name = "Java";              // inferred as String, not dynamic typing
var count = 42;                 // inferred as int
var list = new java.util.ArrayList<String>();
```

`var` may only be used for **local variables with an initializer** — not for fields, method parameters, or return types. For switch expressions, pattern matching, and other modern syntax, see the `modern-syntax` module.

## 2. Operators

| Category | Operators | Notes |
|------|--------|------|
| Arithmetic | `+ - * / %`, `++ --` | Integer division truncates: `7 / 2 == 3` |
| Relational | `== != < > <= >=` | Produces a `boolean` |
| Logical | `&& \|\| !` | **Short-circuits**: if the left side decides the result, the right side is not evaluated |
| Bitwise | `& \| ^ ~` | Bitwise AND/OR/XOR/complement |
| Shift | `<< >> >>>` | `>>>` is unsigned right shift; fills high bits with 0 |
| Ternary | `condition ? a : b` | The two branch types must have a unifying type |
| Type check | `instanceof` | Runtime type check (Java 16+ supports pattern variables) |
| Assignment | `= += -= *= /= %=` etc. | Compound assignment includes an implicit cast |

```java
int max = a > b ? a : b;
boolean ok = (x > 0) && (10 / x > 1); // when x<=0 the right side is not evaluated
if (obj instanceof String s) {        // Java 16+ pattern matching
    System.out.println(s.length());
}
```

Bitwise and shift operators work on the binary representation of integers. `~` is bitwise complement (`~0 == -1`); `>>` preserves the sign bit on right shift, while `>>>` always fills the high bits with 0.

## 3. Control Flow and Loops

Conditional branching uses `if / else if / else`; for multiple branches use `switch` (this module demonstrates the **traditional form** — switch expressions belong to `modern-syntax`):

```java
switch (day) {
    case 1:
        System.out.println("Monday");
        break;               // omitting break "falls through" to the next case
    case 2:
    case 3:                  // multiple labels share one block
        System.out.println("Midweek");
        break;
    default:
        System.out.println("Other");
}
```

The traditional `switch` supports `int`/`char`/`String` (Java 7+) and enums. **Without `break`, execution continues into the next case** (fall-through).

The four loop forms:

```java
for (int i = 0; i < 3; i++) { }        // known iteration count
while (cond) { }                       // test first, then execute
do { } while (cond);                   // execute first, then test; runs at least once
for (String s : list) { }              // for-each: no index, cannot replace elements
```

`break` exits the current loop, `continue` proceeds to the next iteration; labels let you break out of an outer loop:

```java
outer:
for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
        if (j == 1) continue outer; // jump straight to the next outer iteration
        if (i == 2) break outer;    // terminate the outer loop immediately
    }
}
```

## 4. Input and Output

Output goes through `System.out`: `print` (no newline), `println` (with newline), `printf` (formatted):

```java
System.out.print("Result: ");
System.out.println(42);
System.out.printf("%s = %d%n", "Total", 42);
```

Input uses `java.util.Scanner` (the examples embed the data in a string so they run offline):

```java
Scanner sc = new Scanner("3 4\na line of text\n");
int a = sc.nextInt();
int b = sc.nextInt();
sc.nextLine();               // consume the newline left behind by nextInt
String line = sc.nextLine();
```

`next()` reads up to whitespace, `nextLine()` reads a whole line, and `nextInt()`/`nextDouble()` read numeric values. When reading a line after a numeric value, you must first call `nextLine()` to clear the leftover newline from the buffer.

## 5. Running This Module

**VS Code**: open `src/com/learn/basics/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|------|
| `Main.java` | Main entry point; runs all examples in order |
| `VariablesAndTypes.java` | The 8 primitive types, literals, default values, type conversion, var |
| `Operators.java` | Arithmetic, comparison, short-circuit logic, bitwise ops, shifts, ternary, instanceof |
| `ControlFlow.java` | if/switch, the four loop forms, break/continue/labels |
| `InputOutput.java` | System.out output and Scanner string input |

## Common Pitfalls

1. `float f = 3.14;` fails to compile — decimal literals are `double`; you must write `3.14F` or cast explicitly with `(float)`
2. Casting `130` to `byte` yields `-126`: narrowing casts do not error out; they simply wrap around in two's complement
3. Using a local variable before assigning it is a compile error; don't count on a "default 0"
4. `7 / 2` is `3`, not `3.5`; to keep the fraction, one of the operands must be floating point
5. Omitting `break` in a traditional `switch` falls through into the next case
6. Calling `nextLine()` right after `nextInt()` often reads an empty line — the newline is still in the buffer
7. For reference types, `==` compares addresses; use `equals` to compare string contents
