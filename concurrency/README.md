# 并发编程（Concurrency）

Java 从一开始就内建多线程支持。本模块覆盖从 Java 8 到 Java 25 的核心并发原语：Thread、synchronized、Lock/Condition、volatile、原子类、ThreadLocal、线程池（ExecutorService）、CompletableFuture 与并发集合。虚拟线程（Java 21+）在独立模块中专门讲解，这里只做指路。

## 1. Thread 生命周期与 start vs run（Java 8+）

线程状态：`NEW → RUNNABLE → (BLOCKED / WAITING / TIMED_WAITING) → TERMINATED`。

```java
Thread t = new Thread(() -> System.out.println("工作中: " + Thread.currentThread().getName()));
t.start();   // 启动新线程执行 run
t.join();    // 等待 t 结束
```

**关键**：调用 `run()` 只是普通方法调用（仍在当前线程）；`start()` 才会新建线程执行 `run()`。

## 2. Runnable / Callable / Future（Java 8+）

| 类型 | 特点 |
|------|------|
| `Runnable` | 无返回值、不抛受检异常 |
| `Callable<V>` | 有返回值、可抛异常 |
| `Future<V>` | 异步结果句柄：`get()` 阻塞取值、`cancel()` 取消 |

```java
Callable<Integer> task = () -> 40 + 2;
ExecutorService pool = Executors.newSingleThreadExecutor();
Future<Integer> future = pool.submit(task);
System.out.println(future.get());  // 42
pool.shutdown();
```

## 3. synchronized 与 Lock（Java 8+）

`synchronized` 是内置锁（monitor），三种写法：

```java
synchronized void instanceMethod() {}          // 锁当前实例 this
synchronized static void classMethod() {}      // 锁 Class 对象
synchronized (this) { /* 临界区 */ }            // 显式指定锁对象
```

显式锁 `ReentrantLock` + `Condition` 功能更强（可中断、可超时、多条件队列）：

```java
ReentrantLock lock = new ReentrantLock();
Condition notFull = lock.newCondition();
lock.lock();
try {
    while (条件不满足) notFull.await();
    // 干活
} finally {
    lock.unlock();   // 必须 finally 释放
}
```

## 4. wait / notify / notifyAll（Java 8+）

`wait`/`notify` 是 Object 的方法，必须在 `synchronized` 块内调用。新代码更推荐 `Lock`/`Condition`（见上）：`await`/`signal` 语义更清晰，且支持多个条件队列。

```java
synchronized (lock) {
    while (!ready) lock.wait();
    lock.notifyAll();
}
```

**重点**：`wait` 要用 `while` 判断条件（防虚假唤醒），不要用 `if`。

## 5. volatile、原子类与线程安全（Java 8+）

- `volatile`：保证**可见性**与**有序性**，**不保证复合操作的原子性**（`count++` 仍是竞态）。
- 原子类：`AtomicInteger` / `AtomicLong` / `LongAdder`（高并发计数更优，Java 8+）。

```java
volatile boolean running = true;       // 仅布尔开关等标志位够用
AtomicInteger counter = new AtomicInteger();
counter.incrementAndGet();             // 原子自增
LongAdder adder = new LongAdder();     // 高吞吐计数
adder.increment();
```

## 6. ThreadLocal（Java 8+）

每个线程一份独立副本，典型用途：用户会话、日期格式化器、随机数。**用完必须 `remove()`**，否则线程池场景会内存泄漏/脏数据。

```java
static final ThreadLocal<SimpleDateFormat> FMT =
        ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
String s = FMT.get().format(new Date());
FMT.remove();
```

## 7. ExecutorService 线程池（Java 8+）

```java
ExecutorService pool = Executors.newFixedThreadPool(4);   // 固定线程数
ExecutorService cache = Executors.newCachedThreadPool();  // 按需创建
pool.submit(() -> System.out.println("任务"));
pool.shutdown();   // 不再接收新任务
```

- `ScheduledExecutorService`：延迟/周期执行（替代 `Timer`）。
- 虚拟线程池 `Executors.newVirtualThreadPerTaskExecutor()`（Java 21+）见「虚拟线程」模块。
- 生产环境不推荐 `Executors.newFixedThreadPool` 无界队列写法，应显式 `ThreadPoolExecutor` 限制队列容量。

## 8. CompletableFuture（Java 8+）

声明式异步编排：

```java
CompletableFuture.supplyAsync(() -> 21)
        .thenApply(n -> n * 2)                       // 转换
        .thenCombine(CompletableFuture.completedFuture(1), Integer::sum)
        .exceptionally(e -> -1)                      // 异常兜底
        .thenAccept(System.out::println);
```

`allOf` 等待多个 Future 全部完成。

## 9. 并发集合（Java 8+）

`ConcurrentHashMap`、`CopyOnWriteArrayList`、`BlockingQueue`。`ConcurrentHashMap.compute` 原子更新：

```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.compute("key", (k, v) -> v == null ? 1 : v + 1);
```

## 10. 运行本模块

**VS Code**：打开 `src/com/learn/concurrency/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `ThreadBasics.java` | 线程创建、start vs run、join/sleep |
| `RunnableCallable.java` | Runnable / Callable / Future |
| `SyncAndLock.java` | synchronized 与 ReentrantLock |
| `WaitNotifyDemo.java` | wait / notify 与 Condition |
| `ConcurrentUtils.java` | volatile、原子类、竞态演示 |
| `ThreadLocalDemo.java` | ThreadLocal 用法与 remove |
| `ExecutorDemo.java` | ExecutorService 与 ScheduledExecutorService |
| `CompletableFutureDemo.java` | 异步编排 supplyAsync/thenApply/allOf |
| `ConcurrentHashMapDemo.java` | ConcurrentHashMap 与 compute |

## 易错点

1. 调用 `run()` 而不是 `start()`——没有新线程，串行执行
2. `count++` 在多线程下不是原子操作，用 `AtomicInteger` 或加锁
3. `wait`/`notify` 不在 `synchronized` 块内调用会抛 `IllegalMonitorStateException`
4. `wait` 用 `if` 判断条件，虚假唤醒后逻辑错乱——必须 `while`
5. `ReentrantLock.lock()` 后忘记 `unlock()`，改用 try/finally
6. 死锁：两个线程以相反顺序获取两把锁
7. `SimpleDateFormat` 非线程安全，共享实例会解析出错——用 `ThreadLocal` 或 `DateTimeFormatter`
8. 线程池用完不 `shutdown()`，进程无法退出
