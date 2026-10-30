# Reflection

Reflection lets a program inspect a class's structure (fields, methods, constructors, annotations) at **runtime**, and dynamically create objects and invoke methods. It is the cornerstone of frameworks (Spring IoC, Jackson, JUnit, and MyBatis all rely on reflection), but it comes at a cost to performance and encapsulation. This module covers the commonly used reflection capabilities from Java 8 to Java 25.

## 1. Obtaining the Class Object (Java 8+)

Three equivalent ways:

```java
Class<?> c1 = Class.forName("com.learn.reflection.ClassBasics$Demo"); // fully qualified name; may throw ClassNotFoundException
Class<?> c2 = ClassBasics.Demo.class;                                  // resolved at compile time, safest
Class<?> c3 = obj.getClass();                                          // obtained from an instance at runtime
```

Commonly used methods: `getName()`, `getSimpleName()`, `getSuperclass()`, `getInterfaces()`, `isInterface()`, `getModifiers()`.

## 2. Creating Objects: newInstance vs Constructors (Java 8+)

```java
// Obsolete form (marked @Deprecated in Java 9): can only call the no-arg constructor, exceptions are wrapped
Object o1 = cls.newInstance();

// Recommended form (officially recommended since Java 9+):
Object o2 = cls.getDeclaredConstructor().newInstance();
```

`getDeclaredConstructor(parameter types...)` can obtain a constructor of any visibility (including private); you then invoke it with `setAccessible(true)`.

## 3. Field Access (Java 8+)

```java
Field f = cls.getDeclaredField("name");
f.setAccessible(true);   // bypass the private access restriction
f.set(obj, "John Doe");
Object v = f.get(obj);
```

**Modularity note (Java 9+)**: reflective access across module boundaries to JDK internals or packages that are not `opens`-ed throws `InaccessibleObjectException`. Reflective access to your own classes can simply use `setAccessible(true)`; JDK internals require `--add-opens`.

## 4. Method Invocation (Java 8+)

```java
Method m = cls.getDeclaredMethod("greet", String.class); // method name + parameter types
m.setAccessible(true);
Object result = m.invoke(obj, "Java");   // target object + actual arguments
```

If the target method throws, `invoke` wraps it in an `InvocationTargetException`; the real cause is in `getCause()`.

## 5. Generic Information (Java 8+)

Most generics are erased at runtime, but **generics in signatures** are preserved on methods such as `getGenericSuperclass()`:

```java
class StringRepo extends Repo<String> {}
Type superType = StringRepo.class.getGenericSuperclass();
// use ParameterizedType to extract the actual type arguments
```

## 6. Runtime Annotations (Java 8+)

```java
if (cls.isAnnotationPresent(MyAnno.class)) {
    MyAnno a = cls.getAnnotation(MyAnno.class);
    System.out.println(a.value());
}
```

The annotation must be declared `@Retention(RetentionPolicy.RUNTIME)`; see the "Annotations" module for details.

## 7. Dynamic Proxy (Java 8+)

`java.lang.reflect.Proxy` + `InvocationHandler`: generates interface implementations at runtime and uniformly augments methods (a rudimentary form of AOP).

```java
Service proxy = (Service) Proxy.newProxyInstance(
        Service.class.getClassLoader(),
        new Class<?>[] { Service.class },
        (p, method, args) -> {
            System.out.println("Before call: " + method.getName());
            return method.invoke(target, args);
        });
```

Note: `InvocationTargetException` must be unwrapped with `getCause()`; `Proxy` can only proxy interfaces (proxying classes requires a third-party library such as CGLIB).

## 8. Performance, Security, and Common Exceptions

- Performance: reflective calls are an order of magnitude slower than direct calls; use with care on hot paths. `setAccessible(true)` also incurs security-check overhead.
- Security: reflection breaks encapsulation and exposes private implementations; frameworks should constrain it with permissions/the module system.

| Exception | Trigger |
|------|----------|
| `ClassNotFoundException` | wrong class name in `Class.forName` / not on the classpath |
| `NoSuchMethodException` | parameter types do not match in `getMethod`/`getDeclaredMethod` |
| `NoSuchFieldException` | field name does not exist |
| `IllegalAccessException` | no access permission and `setAccessible(true)` not called |
| `InvocationTargetException` | the invoked method threw internally (unwrap with `getCause()`) |
| `InaccessibleObjectException` (Java 9+) | forced setAccessible while the module does not `opens` the package |

## 9. Running This Module

**VS Code**: open `src/com/learn/reflection/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|------|
| `Main.java` | main entry point, runs all examples in order |
| `ClassBasics.java` | obtaining the Class object and class information |
| `Instantiation.java` | creating objects reflectively |
| `FieldAccess.java` | field read/write and setAccessible |
| `MethodInvoke.java` | method lookup and invoke |
| `ConstructorAccess.java` | constructor reflection and private constructors |
| `AnnotationReflect.java` | reading annotations at runtime |
| `ProxyReflect.java` | dynamic proxy with Proxy + InvocationHandler |
| `ReflectPitfalls.java` | common exceptions and performance/module caveats |

## Common Pitfalls

1. `Class.forName("Demo")` without the package name (fully qualified name `com.learn.xxx.Demo`) causes `ClassNotFoundException`
2. Missing or wrong parameter types in `getDeclaredMethod("greet", String.class)` throw `NoSuchMethodException`
3. Forgetting `setAccessible(true)` before reflectively invoking a private member throws `IllegalAccessException`
4. Printing `e` directly when `invoke` throws `InvocationTargetException` hides the real stack trace — use `e.getCause()`
5. `cls.newInstance()` is obsolete and cannot handle parameterized constructors or checked exceptions; use `getDeclaredConstructor().newInstance()` instead
6. Under the Java 9+ module system, forced reflection into JDK internals throws `InaccessibleObjectException`
7. Dynamic proxies can only proxy **interfaces**; classes need a third-party solution such as CGLIB
