# Concurrency

Java has had built-in multithreading support from the start. This module covers the core concurrency primitives from Java 8 to Java 25: Thread, synchronized, Lock/Condition, volatile, atomic classes, ThreadLocal, thread pools (ExecutorService), CompletableFuture, and concurrent collections. Virtual threads (Java 21+) are covered in a dedicated module; they are only mentioned here as a pointer.

## 1. Thread Lifecycle and start vs run (Java 8+)

Thread states: `NEW → RUNNABLE → (BLOCKED / WAITING / TIMED_WAITING) → TERMINATED`.

```java
Thread t = new Thread(() -> System.out.println("Working: " + Thread.currentThread().getName()));
t.start();   // start a new thread that executes run
t.join();    // wait for t to finish
```

**Key point**: calling `run()` is just a normal method call (still on the current thread); only `start()` creates a new thread to execute `run()`.

## 2. Runnable / Callable / Future (Java 8+)

| Type | Characteristics |
|------|------|
| `Runnable` | no return value, cannot throw checked exceptions |
| `Callable<V>` | returns a value, can throw exceptions |
| `Future<V>` | handle to an asynchronous result: `get()` blocks for the value, `cancel()` cancels |

```java
Callable<Integer> task = () -> 40 + 2;
ExecutorService pool = Executors.newSingleThreadExecutor();
Future<Integer> future = pool.submit(task);
System.out.println(future.get());  // 42
pool.shutdown();
```

## 3. synchronized and Lock (Java 8+)

`synchronized` is the built-in lock (monitor); there are three forms:

```java
synchronized void instanceMethod() {}          // locks the current instance, this
synchronized static void classMethod() {}      // locks the Class object
synchronized (this) { /* critical section */ }            // explicitly chooses the lock object
```

The explicit lock `ReentrantLock` + `Condition` is more capable (interruptible, supports timeouts, multiple condition queues):

```java
ReentrantLock lock = new ReentrantLock();
Condition notFull = lock.newCondition();
lock.lock();
try {
    while (condition not met) notFull.await();
    // do the work
} finally {
    lock.unlock();   // must release in a finally block
}
```

## 4. wait / notify / notifyAll (Java 8+)

`wait`/`notify` are methods on Object and must be called inside a `synchronized` block. For new code, prefer `Lock`/`Condition` (see above): `await`/`signal` have clearer semantics and support multiple condition queues.

```java
synchronized (lock) {
    while (!ready) lock.wait();
    lock.notifyAll();
}
```

**Key point**: use `while` to re-check the condition around `wait` (to guard against spurious wakeups), not `if`.

## 5. volatile, Atomic Classes, and Thread Safety (Java 8+)

- `volatile`: guarantees **visibility** and **ordering**, but **not the atomicity of compound operations** (`count++` is still a race).
- Atomic classes: `AtomicInteger` / `AtomicLong` / `LongAdder` (better for high-concurrency counting, Java 8+).

```java
volatile boolean running = true;       // enough for simple boolean flags
AtomicInteger counter = new AtomicInteger();
counter.incrementAndGet();             // atomic increment
LongAdder adder = new LongAdder();     // high-throughput counting
adder.increment();
```

## 6. ThreadLocal (Java 8+)

Each thread gets its own independent copy; typical uses include user sessions, date formatters, and random number generators. **You must call `remove()` when you are done**, otherwise thread pool scenarios will leak memory or see stale data.

```java
static final ThreadLocal<SimpleDateFormat> FMT =
        ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
String s = FMT.get().format(new Date());
FMT.remove();
```

## 7. ExecutorService Thread Pools (Java 8+)

```java
ExecutorService pool = Executors.newFixedThreadPool(4);   // fixed thread count
ExecutorService cache = Executors.newCachedThreadPool();  // creates threads on demand
pool.submit(() -> System.out.println("task"));
pool.shutdown();   // no longer accepts new tasks
```

- `ScheduledExecutorService`: delayed/periodic execution (replaces `Timer`).
- The virtual thread pool `Executors.newVirtualThreadPerTaskExecutor()` (Java 21+) is covered in the "Virtual Threads" module.
- In production, avoid `Executors.newFixedThreadPool` with its unbounded queue; use an explicit `ThreadPoolExecutor` with a bounded queue instead.

## 8. CompletableFuture (Java 8+)

Declarative asynchronous orchestration:

```java
CompletableFuture.supplyAsync(() -> 21)
        .thenApply(n -> n * 2)                       // transform
        .thenCombine(CompletableFuture.completedFuture(1), Integer::sum)
        .exceptionally(e -> -1)                      // fallback on exception
        .thenAccept(System.out::println);
```

`allOf` waits for multiple futures to all complete.

## 9. Concurrent Collections (Java 8+)

`ConcurrentHashMap`, `CopyOnWriteArrayList`, `BlockingQueue`. Atomic update with `ConcurrentHashMap.compute`:

```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.compute("key", (k, v) -> v == null ? 1 : v + 1);
```

## 10. Running This Module

**VS Code**: open `src/com/learn/concurrency/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|------|
| `Main.java` | main entry point, runs all examples in order |
| `ThreadBasics.java` | thread creation, start vs run, join/sleep |
| `RunnableCallable.java` | Runnable / Callable / Future |
| `SyncAndLock.java` | synchronized and ReentrantLock |
| `WaitNotifyDemo.java` | wait / notify and Condition |
| `ConcurrentUtils.java` | volatile, atomic classes, race condition demo |
| `ThreadLocalDemo.java` | ThreadLocal usage and remove |
| `ExecutorDemo.java` | ExecutorService and ScheduledExecutorService |
| `CompletableFutureDemo.java` | async orchestration supplyAsync/thenApply/allOf |
| `ConcurrentHashMapDemo.java` | ConcurrentHashMap and compute |

## Common Pitfalls

1. Calling `run()` instead of `start()` — no new thread is created, everything runs serially
2. `count++` is not atomic across threads; use `AtomicInteger` or a lock
3. Calling `wait`/`notify` outside a `synchronized` block throws `IllegalMonitorStateException`
4. Checking the condition with `if` before `wait`; after a spurious wakeup the logic goes wrong — it must be `while`
5. Forgetting `unlock()` after `ReentrantLock.lock()`; use try/finally instead
6. Deadlock: two threads acquire two locks in opposite order
7. `SimpleDateFormat` is not thread-safe; sharing an instance produces wrong parses — use `ThreadLocal` or `DateTimeFormatter`
8. Not calling `shutdown()` on a thread pool when finished; the process cannot exit
