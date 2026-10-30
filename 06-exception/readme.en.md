# Exception Handling

Exceptions are Java's mechanism for reporting and handling errors: when something goes wrong a `Throwable` object is thrown and the caller catches and handles it as needed, instead of letting the error silently propagate up the call stack. This module covers the Throwable hierarchy, checked/unchecked exceptions, try-catch-finally, try-with-resources, custom exceptions, and handling principles.

## 1. The Throwable Hierarchy

```text
Throwable
├── Error                —— severe JVM-level errors (OutOfMemoryError, StackOverflowError)
└── Exception
    ├── Checked exceptions          —— IOException, SQLException, etc., enforced by the compiler
    └── RuntimeException —— unchecked: NPE, ISE, ClassCastException, etc.
```

In general, do not catch `Error` (it is not recoverable); `Exception` and its subclasses are what a program can handle.

## 2. Checked vs Unchecked Exceptions

| | Checked | Unchecked |
|--|---------------------|-------------------------|
| Extends | `Exception` (excluding RuntimeException) | `RuntimeException` |
| Compiler | **Enforces** try-catch or throws | Not enforced |
| Typical | `IOException` | `NullPointerException` |
| Use when | The caller can reasonably recover (e.g. a file does not exist) | Programming errors / unexpected failures |

```java
static String read() throws IOException {   // checked: must declare or catch
    throw new IOException("Read failed");
}

static int div(int a, int b) {              // unchecked: no declaration required
    return a / b;
}
```

## 3. try-catch-finally

Execution order: `try` → `catch` (when an exception occurs) → `finally` (always runs, except in extreme cases such as JVM shutdown).

```java
try {
    mayFail();
} catch (IOException e) {
    System.out.println(e.getMessage());
} finally {
    System.out.println("Always runs");  // release resources
}
```

**Pitfall**: a `return` inside `finally` overrides the return value from try/catch and also swallows any uncaught exception — never return from a finally block.

## 4. Multi-Catch (Java 7+)

```java
try {
    mayFail();
} catch (IllegalArgumentException | IllegalStateException e) {
    // one branch handles several exception types; e is implicitly final and cannot be reassigned
}
```

With multiple catch clauses, order them **subclass first, superclass last**, otherwise you get a compile error.

## 5. try-with-resources (Java 7)

Resources that implement `AutoCloseable` can be declared in the try header and are closed automatically in **reverse order**:

```java
try (InputStream in = Files.newInputStream(path);
     OutputStream out = Files.newOutputStream(dst)) {
    in.transferTo(out);
}
// in / out are already closed automatically here
```

Java 9+ lets you reference an existing final or effectively final variable directly:

```java
MyResource r = new MyResource();
try (r) { r.work(); }
```

**Suppressed exceptions**: if the try block throws and close() also throws, the close exception does not replace the primary exception — you can retrieve it via `e.getSuppressed()`:

```java
catch (Exception e) {
    e.printStackTrace();
    for (Throwable s : e.getSuppressed()) {
        s.printStackTrace();
    }
}
```

## 6. Custom Exceptions

- Needs to be handled by the caller → extend `Exception` (checked).
- Represents a programming error / business assertion → extend `RuntimeException` (unchecked).
- Exceptions are serializable; declaring `serialVersionUID` explicitly is recommended to preserve version compatibility.

```java
public class InvalidAgeException extends Exception {
    private static final long serialVersionUID = 1L;
    private final int age;
    public InvalidAgeException(int age) {
        super("Invalid age: " + age);
        this.age = age;
    }
}
```

## 7. Rethrowing and Wrapping

When a low-level exception message is meaningless to the caller, wrap it in a higher-level exception and **preserve the original cause**:

```java
try {
    parse();
} catch (NumberFormatException e) {
    throw new IllegalStateException("Config parsing failed", e); // cause preserves the original context
}
```

Print the stack trace with `e.printStackTrace()`, or use a logging framework: `log.error("Failed", e)`; `getStackTrace()` lets you read it programmatically.

## 8. Common Exceptions at a Glance

| Exception | Typical Cause |
|------|----------|
| `NullPointerException` | Dereferencing a null reference (Java 14+ enhanced messages point to the specific variable, making it easy to locate) |
| `ClassCastException` | Incompatible cast |
| `IndexOutOfBoundsException` | Index out of bounds |
| `NumberFormatException` | Failed to parse a string as a number |
| `IllegalArgumentException` | Illegal method argument |
| `ArithmeticException` | Arithmetic error (e.g. division by zero) |
| `UnsupportedOperationException` | Calling a mutator on an unmodifiable collection or one that does not implement it |

## 9. Best Practices

1. **Catch specific exceptions**; avoid the catch-all `catch (Exception e)`.
2. **Don't swallow exceptions**: either handle them, wrap and rethrow, or at least log them.
3. Don't `return` from `finally`; leave resource cleanup to try-with-resources.
4. Don't use exceptions for regular control flow (creating exceptions is costly and obscures the logic).
5. Preserve the cause; don't `throw new XxxException(e.getMessage())` and lose the root cause.

## 10. Running This Module

**VS Code**: open `src/com/learn/exception/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|------|
| `Main.java` | Main entry point; runs all examples in order |
| `TryCatchFinally.java` | Execution order and the trap of return inside finally |
| `CheckedVsUnchecked.java` | The Throwable hierarchy, checked/unchecked exceptions |
| `CustomException.java` | Custom exceptions and serialVersionUID |
| `TryWithResources.java` | Automatic closing and suppressed exceptions |
| `MultiCatchAndFinally.java` | multi-catch, wrapping and rethrowing, stack traces |
| `ExceptionBestPractices.java` | Common exceptions and handling principles |

## Common Pitfalls

1. Writing `return` inside `finally` → overrides the return value and swallows exceptions
2. Catching `catch (Exception e)`, swallowing everything and doing nothing → problems get hidden
3. Ordering multiple catch clauses superclass first, subclass last → compile error (subclass catch must come first)
4. Forgetting `serialVersionUID` on a custom exception → deserialization warnings/failures across versions
5. Dropping the cause when wrapping an exception (passing only the message) → the root-cause stack trace is lost
6. Neither catching a checked exception nor declaring `throws` → compile error; declaring every unchecked exception too makes signatures explode
