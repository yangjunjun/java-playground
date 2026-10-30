# Virtual Threads and New Concurrency Features

Virtual threads (Project Loom) are lightweight threads, standardized in Java 21: scheduled by the JVM rather than the OS, extremely cheap to create, bringing the thread-per-task style back into the mainstream. This module also covers structured concurrency with `StructuredTaskScope` (**still a preview in JDK 25**) and scoped values with `ScopedValue` (**standard in Java 25**).

## 1. Why Virtual Threads? (Java 21+)

| | Platform threads | Virtual threads |
|---|---|---|
| Scheduler | OS | JVM (mounted on a few carrier threads) |
| Stack memory | ~1MB per thread (reserved by the OS) | Grows on the heap on demand, starting at a few KB |
| Creating 1 million | Infeasible (would need terabytes of RAM) | Feasible (tens of MB) |
| Cost of blocking | Ties up an OS thread | Unmounts the carrier, nearly free |

**Good for**: IO-bound work with massive numbers of concurrent connections (HTTP calls, databases, RPC).
**Not good for**: CPU-bound computation — virtual threads optimize "blocking", not "computing".

## 2. Creating Virtual Threads (Java 21+)

Three ways:

```java
// option 1: Thread.ofVirtual() builder
Thread t = Thread.ofVirtual().name("vt-1").start(() -> System.out.println("hello"));
t.join();

// option 2: convenience method
Thread.startVirtualThread(() -> System.out.println("hello"));

// option 3: thread-per-task executor (recommended)
try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
    exec.submit(() -> System.out.println("hello"));
}
```

To tell whether a thread is virtual: `Thread.currentThread().isVirtual()`.

## 3. Thread-per-Task (Java 21+) — Do Not Pool Virtual Threads

Old mindset: platform threads are expensive → pool and reuse them.
New mindset: virtual threads are cheap → **create on demand, discard when the task finishes**.

```java
// correct: one virtual thread per task
try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
    for (var task : tasks) {
        exec.submit(task);
    }
}

// wrong: pooling virtual threads (adds queuing, defeats the concurrency)
Executors.newFixedThreadPool(100); // even 100 virtual threads — don't pool them
```

For CPU-bound work, still use a fixed platform thread pool sized to the core count:

```java
Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
```

## 4. Mounting vs Pinning

At run time a virtual thread **mounts** onto a carrier thread (carrier count defaults to the CPU core count).
When it blocks, it can **unmount**, freeing the carrier for other virtual threads.

**Pinning**: certain operations prevent unmounting; the carrier stays occupied and other virtual threads wait.

| Scenario | Pins? |
|------|----------|
| `Thread.sleep()` / IO blocking | No (unmounts) |
| Blocking inside `synchronized` | **Pinned before JDK 24**; mitigated in JDK 24+ (JEP 491) |
| Blocking in JNI / native methods | Still pins |
| Holding a `synchronized` critical section for a long time | Still a concern |

Diagnosing pinning:

```bash
java -Djdk.tracePinnedThreads=full ...
```

Advice: prefer `java.util.concurrent.locks.ReentrantLock`; avoid blocking inside `synchronized`.

## 5. Structured Concurrency with StructuredTaskScope (Java 21 preview → **still preview in JDK 25**)

Confines the lifetimes of a group of subtasks to a try-with-resources scope: **all must finish before the scope exits**, eliminating task leaks and runaway threads.

> **Important (verified on JDK 25.0.2)**: `java.util.concurrent.StructuredTaskScope` is **still a preview API** in JDK 25;
> both compilation and execution require `--enable-preview`. Therefore this module's `StructuredConcurrencyDemo.java`
> implements the equivalent semantics with "virtual threads + CompletableFuture", so `./run.sh` runs out of the box on a stock JDK 25.
> Below is the full preview-API form, for reference when you do pass `--enable-preview`.

**API shape (Java 25 settled on `Joiner`; the `ShutdownOnFailure` nested class is gone)**:

```java
// collect all successful subtasks
try (var scope = StructuredTaskScope.open(Joiner.<String>allSuccessfulOrThrow())) {
    Subtask<String> u = scope.fork(() -> fetch("user"));
    Subtask<String> o = scope.fork(() -> fetch("order"));
    Stream<Subtask<String>> all = scope.join();   // throws if any fail
    all.forEach(s -> System.out.println(s.get()));
}

// shutdown-on-failure: first failure cancels the rest and is thrown from join()
try (var scope = StructuredTaskScope.open(Joiner.<String>awaitAllSuccessfulOrThrow())) {
    scope.fork(() -> fetch("user"));
    scope.fork(() -> fetch("bad"));
    scope.join();
}

// take the first successful result (like ExecutorCompletionService / invokeAny)
try (var scope = StructuredTaskScope.open(Joiner.<String>anySuccessfulResultOrThrow())) {
    scope.fork(() -> slow());
    scope.fork(() -> fast());
    String first = scope.join();
}
```

Compiling and running the preview version:

```bash
javac --enable-preview --release 25 ...
java  --enable-preview ...
```

Compared to hand-rolled `Future` code: no `shutdown()` needed, exceptions propagate automatically, and tasks are guaranteed to finish.

**Run-time alternative (no preview needed; what this module uses)**:

```java
try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
    Future<String> u = exec.submit(() -> fetch("user"));
    Future<String> o = exec.submit(() -> fetch("order"));
    System.out.println(u.get() + o.get());
}
```

## 6. Scoped Values (Java 21 preview → standard in Java 25)

Binds a value to threads (including child threads) within a scope; the binding expires automatically when the scope ends:

```java
private static final ScopedValue<String> USER = ScopedValue.newInstance();

ScopedValue.where(USER, "alice").run(() -> {
    System.out.println(USER.get());   // alice
    inner();                          // nested calls can read it too
});
USER.isBound();   // false after leaving the scope
USER.orElse("anonymous");

// chaining multiple values
ScopedValue.where(USER, "bob").where(TRACE_ID, "trace-1").run(() -> { ... });

// with a return value
Integer len = ScopedValue.where(USER, "dave").call(() -> USER.get().length());
```

**Be careful with thread inheritance**: a ScopedValue binding is **inherited only by subtasks of structured concurrency (`StructuredTaskScope.fork`)**.
Threads started with plain `Thread.start()` / `Thread.ofVirtual().start()` **cannot see** the binding (verified empirically):

```java
ScopedValue.where(USER, "carol").run(() -> {
    Thread.ofVirtual().start(() ->
        USER.orElse("unavailable")   // plain threads don't inherit it
    );
});
// but subtasks of StructuredTaskScope.fork CAN read USER
```

### ScopedValue vs ThreadLocal

| | ThreadLocal | ScopedValue |
|---|---|---|
| Mutability | Can set any value | Immutable once bound |
| Cleanup | Must `remove()` manually or it leaks | Expires automatically |
| Subtask inheritance | Not inherited by default | Inherited by `StructuredTaskScope.fork` subtasks |
| Thread-pool safety | Reuse easily leaks data across tasks | Naturally safe |

**Prefer ScopedValue**; use ThreadLocal sparingly.

## 7. Concurrency Pitfalls and Best Practices

**Pitfalls**:

1. Non-atomic increment `counter++` → lost updates (virtual threads raise the concurrency level and expose this more easily)
2. Not calling `remove()` on `ThreadLocal` when done → leaks in thread-pool scenarios
3. Using virtual threads for CPU-bound tasks → no benefit, possibly slower
4. Busy-waiting on shared state with `sleep` → use `CountDownLatch` / `CompletableFuture` / structured concurrency instead
5. Pooling virtual threads inside virtual threads → deadlock risk

**Best practices**:

```java
// 1. create virtual threads on demand
try (var exec = Executors.newVirtualThreadPerTaskExecutor()) { ... }

// 2. fixed platform thread pool for CPU-bound work
Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

// 3. atomics / concurrent collections for shared state
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
AtomicInteger counter = new AtomicInteger();

// 4. ScopedValue for passing context
// 5. StructuredTaskScope for subtask lifetimes
```

## Running This Module

**VS Code**: open `src/com/learn/vthreads/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|------|
| `Main.java` | Main entry point; runs all examples in sequence |
| `VirtualThreadBasics.java` | Three creation styles, a 100K-virtual-thread experiment |
| `ThreadPerTaskExecutorDemo.java` | Thread-per-task vs thread pool comparison |
| `MountingVsPinning.java` | Mounting/unmounting, synchronized pinning explained |
| `StructuredConcurrencyDemo.java` | Preview API notes + equivalent CompletableFuture implementation |
| `ScopedValuesDemo.java` | Binding, inheritance boundaries, comparison with ThreadLocal |
| `ConcurrencyPitfalls.java` | Common pitfalls and best-practice checklist |

## Common Pitfalls

1. **Never pool virtual threads** — creation is nearly free; pooling just adds queuing and confusion
2. Don't use virtual threads for CPU-bound work; use a fixed platform thread pool
3. Blocking inside `synchronized` pins the carrier thread **before JDK 24**; upgrade to 24+ or switch to `ReentrantLock`
4. Always `remove()` `ThreadLocal` when done; even though virtual threads aren't reused, prefer `ScopedValue`
5. A `ScopedValue` binding is immutable; to change the value you must override it in a new `where(...)` scope
6. `StructuredTaskScope` is **still a preview API in JDK 25**; `--enable-preview` is required. This module's runnable example has been switched to an equivalent CompletableFuture implementation
7. A `ScopedValue` binding is inherited only by `StructuredTaskScope.fork` subtasks; plain `Thread.start()` threads can't see it
8. `StructuredTaskScope` is try-with-resources: subtasks are guaranteed to finish before the scope exits — don't let the scope escape the method
9. An exception thrown by a subtask in structured concurrency makes `join()` throw; remember to catch it or declare `throws`
10. `Thread.sleep` is cheap on virtual threads, but still avoid pointless busy-wait polling
