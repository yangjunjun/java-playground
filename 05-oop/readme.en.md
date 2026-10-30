# Object-Oriented Programming

Object-oriented programming is Java's core paradigm: data (fields) and behavior (methods) are encapsulated in "classes", and objects collaborate to get business logic done. Its four pillars are encapsulation, inheritance, polymorphism, and abstraction. This module covers classes and constructors, access levels, inheritance and overriding, dynamic dispatch, abstract classes/interfaces, Object methods, and inner classes, and briefly introduces newer syntax such as sealed classes.

## 1. Classes and Objects

A class is a template; an object is an instance. `new` calls a constructor to create an object:

```java
class Point {
    int x, y;
    Point(int x, int y) {
        this.x = x; // this refers to the current object
        this.y = y;
    }
}
Point p = new Point(1, 2);
```

## 2. Constructors and Constructor Chaining

- A constructor has the same name as its class, has no return value, and can be overloaded.
- `this(...)` delegates to another constructor in the same class; `super(...)` invokes a superclass constructor.
- The implicit `super()` requires the superclass to have a no-arg constructor; **`super()/this(...)` must be the first statement in a subclass constructor**.
- Construction order: superclass fields/constructors first, then the subclass ("parents before children").

```java
class Account {
    Account(String owner) {
        this(owner, 0); // constructor chaining avoids duplicated initialization
    }
    Account(String owner, double balance) { /* ... */ }
}
```

## 3. Access Levels

| Modifier | Same class | Same package | Subclass | Other |
|--------|:----:|:----:|:----:|:----:|
| `private` | √ | × | × | × |
| Default (none) | √ | √ | × | × |
| `protected` | √ | √ | √ | × |
| `public` | √ | √ | √ | √ |

Fields are usually `private`, exposing only the necessary methods publicly — that is encapsulation.

## 4. Encapsulation and getters/setters

```java
class BankAccount {
    private double balance;
    public double getBalance() { return balance; }
    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        balance += amount;
    }
}
```

The value of encapsulation: hide the implementation, centralize validation, and freely change the internal representation later without affecting callers.

## 5. Inheritance

`extends` expresses an is-a relationship; a subclass can override (`@Override`) superclass methods and call the superclass implementation with `super.`.

```java
class Dog extends Animal {
    @Override
    void speak() {
        super.speak();       // call the superclass version
        System.out.println("Woof woof");
    }
}
```

- A `final` method cannot be overridden; a `final` class cannot be subclassed (e.g. `String`).
- Single inheritance: a class can `extends` only one superclass, but may `implements` multiple interfaces.

## 6. Polymorphism

A superclass reference can point to a subclass object (upcasting); at runtime the method is dispatched according to the actual type (dynamic dispatch):

```java
Shape s = new Circle(2);
s.area(); // calls Circle.area()

// instanceof pattern matching (Java 16+): type check and cast in one step
if (s instanceof Circle c) {
    System.out.println(c.r);
}
```

`switch` pattern matching (Java 21+) simplifies type-based branching even further:

```java
String desc = switch (s) {
    case Circle c -> "circle r=" + c.r;
    case Rect r   -> "rectangle " + r.w + "x" + r.h;
    default       -> "other";
};
```

## 7. Abstract Classes and Interfaces

| Aspect | Abstract class `abstract class` | Interface `interface` |
|------|------------------------|------------------|
| Fields | May have instance fields | Only `public static final` constants |
| Constructors | Yes | None |
| Inheritance | Single inheritance | Any number can be implemented |
| Methods | Concrete or abstract | Abstract + `default` (Java 8) + `static` (Java 8) + `private` (Java 9) |

```java
interface Greeter {
    String greet(String name);              // abstract
    default String polite(String name) {    // Java 8: with a default implementation
        return "(" + greet(name) + ")";
    }
    static String shout(String s) { return s + "!"; } // Java 8
    private String wrap(String s) { return "[" + s + "]"; } // Java 9: for use inside the interface only
}
```

When multiple interfaces provide same-named `default` methods, the implementing class must override them, and can pick one with `InterfaceName.super.method()`.

**Sealed classes/interfaces (sealed, Java 17+)** let you precisely restrict the set of subclasses — ideal for modeling a closed type hierarchy:

```java
sealed interface Shape permits Circle, Rect {}
final class Circle implements Shape {}
final class Rect implements Shape {}
```

## 8. Common Object Methods

| Method | Description |
|------|------|
| `toString()` | String representation; without an override it is `ClassName@hash` |
| `equals(Object)` | Logical equality; **if you override it you must also override `hashCode()`** |
| `hashCode()` | Hash value; two objects that are `equals` must have the same hash |
| `clone()` | Shallow copy; requires `implements Cloneable` and widening `clone()` to `public` |
| `finalize()` | **Deprecated for removal (Java 18+ `@Deprecated(forRemoval)`) and must not be used for resource release**; use try-with-resources or `Cleaner` (Java 9+) instead |

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Point p)) return false;
    return x == p.x && y == p.y;
}
@Override
public int hashCode() { return 31 * x + y; }
```

## 9. Inner Classes

| Type | Characteristics |
|------|------|
| Member inner class | Tied to an outer instance; can access outer private members; created with `outer.new Inner()` |
| Static nested class | No reference to an outer instance; effectively "just happens to live inside another class" |
| Local class | Defined inside a method; scoped to that method |
| Anonymous class | One-off implementation of an interface/abstract class with no class name (the precursor to lambdas) |

```java
class Outer {
    private String msg = "hi";
    class Inner { void show() { System.out.println(msg); } }   // member inner class
    static class Nested { }                                    // static nested
    void m() {
        class Local { }                                        // local inner class
        Runnable r = new Runnable() {                          // anonymous inner class
            public void run() { System.out.println("run"); }
        };
    }
}
```

## 10. Running This Module

**VS Code**: open `src/com/learn/oop/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|------|
| `Main.java` | Main entry point; runs all examples in order |
| `Encapsulation.java` | Classes, constructor chaining, access levels, getters/setters |
| `Inheritance.java` | Inheritance, overriding, super, final |
| `Polymorphism.java` | Upcasting, dynamic dispatch, instanceof/switch pattern matching |
| `AbstractInterface.java` | Abstract classes and interfaces (default/static/private methods) |
| `InnerClasses.java` | Member/static nested/local/anonymous inner classes |
| `ObjectMethods.java` | toString / equals / hashCode / clone |

## Common Pitfalls

1. `super(...)`/`this(...)` not the first statement in a constructor → compile error
2. Overriding `equals` without also overriding `hashCode` → misbehavior when used as a HashMap key
3. Forgetting `implements Cloneable` when using `clone()` → `CloneNotSupportedException` at runtime
4. Narrowing the access level when overriding (e.g. public → private) → compile error
5. Releasing resources in `finalize()` → unreliable and deprecated; use try-with-resources
6. Using `new Inner()` instead of `outer.new Inner()` for a member inner class → compile error
