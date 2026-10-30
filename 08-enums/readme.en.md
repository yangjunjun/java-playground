# Enums

Enums are special classes introduced in Java 5: they express a fixed set of values (weekdays, states, operators) as a finite set of **named constants**. Each constant is a singleton instance of the class — inherently thread-safe, serializable, and comparable. This module covers enum basics, fields and methods, abstract methods and interfaces, EnumSet/EnumMap, and common pitfalls.

## 1. What an Enum Really Is

An `enum` is "a final class that extends `java.lang.Enum`", and its constants are `public static final` instances:

```java
enum Season { SPRING, SUMMER, AUTUMN, WINTER }
```

- You cannot `new Season(...)`, and it cannot be subclassed (it is already final).
- `Season.class.getSuperclass()` is `java.lang.Enum`.

## 2. name / ordinal / values / valueOf

| Method | Description |
|------|------|
| `name()` | The declared constant name (a String); don't rename it lightly |
| `ordinal()` | Declaration order (starting at 0); **for internal use by EnumSet/EnumMap only — never treat it as a business value** |
| `values()` | Returns all constants in declaration order (an array) |
| `valueOf(String)` | Resolves by name; throws `IllegalArgumentException` if not found (case-sensitive) |

```java
Season s = Season.SUMMER;
s.name();          // "SUMMER"
s.ordinal();       // 1
Season.values();   // Season[4]
Season.valueOf("WINTER");
```

## 3. Fields, Constructors, and Methods

Enums can have fields, methods, and constructors (constructors **must be private**; the modifier can be omitted):

```java
enum Planet {
    MERCURY(3.303e+23, 2.4397e6),
    EARTH(5.976e+24, 6.37814e6);

    private final double mass, radius;
    Planet(double mass, double radius) { // private can be omitted
        this.mass = mass; this.radius = radius;
    }
    double surfaceGravity() { /* ... */ return 0; }
}
```

## 4. Abstract Methods and Interfaces

- Enums can declare abstract methods, implemented separately by each **constant-specific class body**;
- An enum **cannot extend a class** (it implicitly extends `Enum`), but it **can implement interfaces**.

```java
enum Operation {
    PLUS {
        double apply(double x, double y) { return x + y; }
    },
    MINUS {
        double apply(double x, double y) { return x - y; }
    };
    abstract double apply(double x, double y);
}

interface Describable { String describe(); }
enum Color implements Describable {
    RED { public String describe() { return "red"; } }
}
```

## 5. EnumSet and EnumMap

| Type | Implementation | Advantage |
|------|------|------|
| `EnumSet<E>` | Bit vector | Faster and more memory-efficient than `HashSet` |
| `EnumMap<K extends Enum<K>, V>` | Array | Faster than `HashMap`, indexed by ordinal |

```java
EnumSet<Day> weekend = EnumSet.of(Day.SAT, Day.SUN);
EnumMap<Day, String> plan = new EnumMap<>(Day.class);
```

## 6. Enums in switch

```java
switch (s) {
    case CIRCLE -> "circle";      // the enum-name prefix can be omitted
    case RECT   -> "rectangle";
}
```

Switch pattern matching (Java 21+) can be combined with `when` guards:

```java
String desc = switch (obj) {
    case Shape c when c == Shape.CIRCLE -> "circle constant";
    case Shape r -> "other shape: " + r;
    default -> "not a Shape";
};
```

## 7. When to Use an Enum vs Constants

| Scenario | Recommendation |
|------|------|
| Fixed, mutually exclusive values (weekdays, states, operators) | **enum** |
| Extra behavior/data needed, polymorphic dispatch per constant | **enum + constant-specific class body** |
| Continuous numeric ranges, externally extensible configuration | Plain class / int — don't force an enum |
| int constant `STATUS_ON = 1` | Lacks type safety — any int can be passed; use an enum |

Comparison and serialization: enums **naturally implement `Comparable` (by ordinal) and `Serializable`**, and serialization has special protection (deserialization never creates new instances). `==` and `equals()` are equivalent.

## 8. Running This Module

**VS Code**: open `src/com/learn/enums/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|------|
| `Main.java` | Main entry point; runs all examples in order |
| `EnumBasics.java` | name/ordinal/values/valueOf |
| `EnumWithFields.java` | Fields, constructors, methods |
| `EnumMethods.java` | compareTo, equals, serializable/comparable |
| `EnumAbstractAndInterface.java` | Constant-specific class bodies, implementing interfaces |
| `EnumPatterns.java` | EnumSet/EnumMap, switch and pattern matching |
| `EnumPitfalls.java` | The ordinal trap and selection advice |

## Common Pitfalls

1. Using `ordinal()` as a business code / persisted value → data breaks when constants are reordered (use an explicit code field)
2. `valueOf("winter")` fails → it is case-sensitive and must match the declared name exactly
3. Writing `extends` or a public constructor for an enum → compile error (it implicitly extends Enum and constructors must be private)
4. Expecting `switch` to flag new constants but writing a `default` that swallows them → new constants go unnoticed (consider an exhaustive switch without default)
5. Using `EnumMap` with non-enum keys, or reaching for `HashMap<Day,...>` for performance → EnumMap is the better fit
6. Making an enum out of extensible external values (user-defined tags) → it cannot be extended; use a plain class or configuration instead
