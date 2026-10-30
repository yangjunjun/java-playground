# Lambda and Functional Interfaces

Lambda expressions, introduced in **Java 8**, are anonymous function syntax that treats "behavior" as a value you can pass around, replacing much of the boilerplate of anonymous inner classes. The type of a lambda is determined by its **target typing**: it must be assigned to a functional interface (an interface with a single abstract method). Combined with the `java.util.function` package and method references, lambdas let you write concise, composable, declarative code.

## 1. Lambda Syntax and Target Typing

Basic syntax: `(params) -> expression` or `(params) -> { statements; }`.

```java
Runnable r = () -> System.out.println("no args");
StringFunc upper = s -> s.toUpperCase();      // single param: () can be omitted, type inferred
IntFunc square = x -> x * x;                  // expression body
IntFunc f = x -> { int a = Math.abs(x); return a + 1; }; // block body needs explicit return
BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b; // multiple params require ()
```

A lambda has no type of its own; it is inferred from the assignment target:

```java
names.sort((x, y) -> x.length() - y.length()); // target type is Comparator
```

## 2. @FunctionalInterface

An interface with a single abstract method (SAM) is a **functional interface**; `default`/`static` methods do not count. Annotate it with `@FunctionalInterface` to have the compiler enforce this.

```java
@FunctionalInterface
interface Calculator {
    int calc(int a, int b);
    default int inc(int x) { return calc(x, 1); }
    static int zero() { return 0; }
}
```

**Overload ambiguity**: when two overloaded methods take different functional interfaces, a bare lambda cannot infer a target type, so cast explicitly:

```java
overloaded((Foo) s -> System.out.println(s)); // compile error: overloaded(s -> ...)
```

## 3. The Four Forms of Method Reference

| Form | Syntax | Equivalent lambda |
|------|--------|-------------------|
| Static method reference | `ClassName::staticMethod` | `(a) -> ClassName.staticMethod(a)` |
| Unbound instance method reference | `ClassName::instanceMethod` | `(a) -> a.instanceMethod()` |
| Bound instance method reference | `object::instanceMethod` | `(a) -> object.instanceMethod(a)` |
| Constructor reference | `ClassName::new` | `(a) -> new ClassName(a)` |

```java
UnaryOperator<String> shout = MethodReferences::shout; // static
Function<String, String> upper = String::toUpperCase;  // unbound instance
Function<String, String> bound = prefix::concat;       // bound instance
Supplier<ArrayList<String>> ctor = ArrayList::new;     // constructor
BiFunction<String, Integer, Person> personCtor = Person::new;
```

## 4. Functional Composition

`Function` provides `andThen` (this first, then the argument) and `compose` (argument first, then this); `Predicate` provides `and` / `or` / `negate`.

```java
Function<Integer, Integer> doubleIt = x -> x * 2;
Function<Integer, Integer> plusOne = x -> x + 1;

doubleIt.andThen(plusOne).apply(5); // 11: *2 first, then +1
doubleIt.compose(plusOne).apply(5); // 12: +1 first, then *2

hasA.and(hasB).test("abc");
hasA.negate().test("xyz");
```

## 5. Variable Capture and `this` Semantics

A lambda may only capture **effectively final** local variables and parameters (the value must never change; you do not have to write `final`). Fields are unrestricted and are accessed through `this`.

```java
int base = 10;
// base = 20;           // remove the comment and compilation fails
IntUnaryOperator op = x -> x + base;
```

**`this` semantics**: a lambda has no `this` of its own — `this` is the enclosing instance; an anonymous inner class has its own `this`, which shadows the outer one.

What is captured is the **reference**: the reference itself cannot be reassigned, but the object it points to can still be mutated.

## 6. Built-in Functional Interfaces (java.util.function)

| Interface | Signature | Purpose |
|-----------|-----------|---------|
| `Function<T,R>` | `T -> R` | Transform |
| `BiFunction<T,U,R>` | `(T,U) -> R` | Transform two values |
| `Predicate<T>` | `T -> boolean` | Test |
| `Supplier<T>` | `() -> T` | Lazily supply a value |
| `Consumer<T>` | `T -> void` | Consume a value |
| `BiConsumer<T,U>` | `(T,U) -> void` | Consume two values |
| `UnaryOperator<T>` | `T -> T` | Unary operator |
| `BinaryOperator<T>` | `(T,T) -> T` | Binary operator |
| `IntFunction<R>` / `IntUnaryOperator` | `int -> R` / `int -> int` | Primitive specializations, avoid boxing |

Typical uses:

```java
names.sort(Comparator.comparing(String::length));
names.removeIf(s -> s.startsWith("B"));
map.computeIfAbsent("fruit", k -> new ArrayList<>()).add("apple");
```

## 7. Running This Module

**VS Code**: open `src/com/learn/lambda/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|----------|
| `Main.java` | Main entry point, runs all examples in order |
| `LambdaSyntax.java` | Syntax forms and target typing |
| `FunctionalInterfaces.java` | `@FunctionalInterface` and overload ambiguity |
| `MethodReferences.java` | The four forms of method reference |
| `FunctionalComposition.java` | andThen / compose / and / or / negate |
| `LambdaCapture.java` | effectively final and `this` semantics |
| `BuiltInFunctionals.java` | java.util.function and typical collection usage |

## Common Pitfalls

1. Forgetting `return` in a multi-line block lambda (expression bodies do not need it; block bodies do)
2. Modifying a captured local variable breaks compilation — it must be effectively final
3. Thinking `this` inside a lambda refers to the lambda itself; a lambda's `this` is the enclosing instance
4. When overloaded methods all take functional interfaces, a bare lambda reports "ambiguous target type" — cast explicitly
5. Reversing `andThen` and `compose`: `A.andThen(B)` runs A then B, `A.compose(B)` runs B then A
6. The return type of `UnaryOperator.andThen(...)` is `Function`, not `UnaryOperator` (`andThen` is declared on `Function`) — declare the assignment type accordingly
7. Using boxed `Function<Integer,Integer>` instead of `IntUnaryOperator` on large data sets, paying needless boxing overhead
8. Hand-writing a SAM with an anonymous inner class when a lambda or method reference would simplify it
