# Generics

Generics is the parameterized-type mechanism introduced in Java 5: it treats "types" as parameters, performing type checking at compile time and eliminating casts, while letting a single collection or utility implementation serve many types. This module covers generic classes and interfaces, generic methods, bounded parameters, wildcards and PECS, type erasure and its consequences.

## 1. Generic Classes and Interfaces

Type parameters go after the class name, by convention single letters: `T` (Type), `K`/`V` (Key/Value), `E` (Element), `N` (Number).

```java
class Box<T> {
    private T content;
    void put(T item) { this.content = item; }
    T get() { return content; }
}

interface Repository<T> {
    void save(T entity);
    T findById(int id);
}
```

Supply a type argument at the use site: `Box<String> box = new Box<>();` (the `<>` on the right is the diamond operator; Java 7+ lets you omit the type argument there).

## 2. Generic Methods

`<T>` goes before the return type; the type parameter is only in scope for that method and can usually be supplied by the compiler's **type inference**:

```java
static <T> T firstOf(List<T> list) {
    return list.isEmpty() ? null : list.get(0);
}
String s = firstOf(List.of("a", "b"));   // T inferred as String
```

**varargs + generics** produce a "non-reifiable type" warning; if you are sure you never put unsafe elements into the varargs array, you can annotate with `@SafeVarargs` (Java 7+, allowed only on static/final methods and constructors):

```java
@SafeVarargs
static <T> List<T> asList(T... items) {
    return List.of(items);
}
```

`List.of(...)` (Java 9+): returns an immutable list and rejects null, making it a good fit for return values and constants.

## 3. Bounded Type Parameters

`<T extends Bound>` constrains the upper bound of a type parameter (`extends` is used for both classes and interfaces), so you can safely call methods of the bound:

```java
static <T extends Comparable<T>> T max(List<T> list) {
    T best = list.get(0);
    for (T item : list) {
        if (item.compareTo(best) > 0) best = item;
    }
    return best;
}
```

Multiple bounds: `<T extends Number & Comparable<T>>` — **class first, interfaces after**, and there can be only one class.

## 4. Wildcards and PECS

| Syntax | Meaning | Read | Write |
|------|------|------|------|
| `List<? extends Number>` | Upper-bounded wildcard (covariant) | Read as `Number` | Cannot add concrete elements |
| `List<? super Integer>` | Lower-bounded wildcard (contravariant) | Read only as `Object` | Can add `Integer` and its subclasses |
| `List<?>` | Unbounded wildcard | Read as `Object` | Cannot add (except null) |

```java
static double sumOf(List<? extends Number> list) { ... } // producer: read-only
static void fill(List<? super Integer> list) { list.add(1); } // consumer: writable
```

**The PECS principle**: Producer Extends, Consumer Super — use `? extends` when taking data out of a collection, `? super` when putting data in.

Note: `List<String>` is **not** a subtype of `List<Object>` (generics are invariant); use a wildcard when you need a supertype container.

## 5. Type Erasure

Generic information exists **only at compile time** and is erased to the upper bound afterwards (default `Object`):

- `List<String>` and `List<Integer>` have the same runtime `getClass()`;
- You cannot `new T[]` or `new T()`;
- You cannot `instanceof List<String>` (only `instanceof List` or `List<?>`);
- When overriding a generic method, the compiler generates a **bridge method** to preserve polymorphism.

```java
// What the compiler actually generates:
// void store(Object obj) { store((String) obj); }   // bridge
```

## 6. Generic Pitfalls and Best Practices

1. **Never use raw types** (`List list`): they bypass type checking and cause `ClassCastException` at runtime.
2. Primitive types cannot be type arguments: use `List<Integer>` rather than `List<int>` (autoboxing has a cost).
3. You cannot create generic arrays `new Box<String>[10]` — use `List<Box<String>>` instead.
4. You cannot instantiate `T` — pass a `Supplier<T>` when you need a factory.
5. When a method returns a collection, **return an empty collection (`List.of()`) instead of null**.
6. When in doubt about wildcard bounds, remember PECS.

## 7. Running This Module

**VS Code**: open `src/com/learn/generics/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|------|
| `Main.java` | Main entry point; runs all examples in order |
| `GenericClass.java` | Generic classes/interfaces + a practical Repository example |
| `GenericMethod.java` | Generic methods, type inference, @SafeVarargs, List.of |
| `BoundedTypes.java` | Bounded type parameters and multiple bounds |
| `Wildcards.java` | ? extends / ? super / PECS |
| `TypeErasure.java` | Consequences of type erasure and bridge methods |
| `GenericPitfalls.java` | Raw types, generic arrays, and other pitfalls |

## Common Pitfalls

1. Assigning `List<String>` to `List<Object>` → compile error (generics are invariant; use `?`)
2. Mixing different types in a raw `List` → compiles fine but blows up with a cast at runtime
3. Calling `add(1)` on a `List<? extends Number>` → compile error (upper-bounded wildcards are read-only)
4. Getting the parameter type wrong when overriding a generic superclass method → not a real override but an overload (@Override helps catch this)
5. Putting `@SafeVarargs` on a regular (non-final) instance method → compile error
6. Expecting `List<String>.class` to differ from `List<Integer>.class` → they are actually identical (type erasure)
