# 虚拟线程与并发新特性（Virtual Threads）

虚拟线程（Project Loom）是 Java 21 正式引入的轻量线程：由 JVM 调度而非操作系统，创建成本极低，让「每个任务一个线程」的写法重新成为主流。本模块还覆盖结构化并发 `StructuredTaskScope`（**JDK 25 仍为预览**）与作用域值 `ScopedValue`（**Java 25 正式**）。

## 1. 为什么需要虚拟线程（Java 21+）

| | 平台线程 | 虚拟线程 |
|---|---|---|
| 调度者 | 操作系统 | JVM（挂在少量载体线程上） |
| 栈内存 | 约 1MB/线程（OS 预留） | 堆上按需增长，几 KB 起步 |
| 创建 100 万个 | 不可行（需 TB 级内存） | 可行（几十 MB） |
| 阻塞成本 | 占住 OS 线程 | 卸载载体，几乎零成本 |

**适用**：IO 密集、海量并发连接（HTTP 调用、数据库、RPC）。
**不适用**：CPU 密集计算——虚拟线程只优化「阻塞」，不优化「计算」。

## 2. 创建虚拟线程（Java 21+）

三种方式：

```java
// 方式 1：Thread.ofVirtual() 构建器
Thread t = Thread.ofVirtual().name("vt-1").start(() -> System.out.println("hello"));
t.join();

// 方式 2：快捷方法
Thread.startVirtualThread(() -> System.out.println("hello"));

// 方式 3：每任务一线程执行器（推荐）
try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
    exec.submit(() -> System.out.println("hello"));
}
```

判断是否虚拟线程：`Thread.currentThread().isVirtual()`。

## 3. 每任务一线程（Java 21+）—— 不要池化虚拟线程

旧思维：平台线程昂贵 → 池化复用。
新思维：虚拟线程廉价 → **随建随用，任务结束即销毁**。

```java
// 正确：每任务一虚拟线程
try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
    for (var task : tasks) {
        exec.submit(task);
    }
}

// 错误：池化虚拟线程（引入排队，破坏并发）
Executors.newFixedThreadPool(100); // 即使 100 个虚拟线程也别池化
```

CPU 密集型仍用固定平台线程池，大小 = 核数：

```java
Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
```

## 4. 挂载与钉住（Mounting vs Pinning）

虚拟线程运行时**挂载**（mount）到载体线程（carrier，数量默认 = CPU 核数）。
阻塞时可**卸载**（unmount），把载体让给其他虚拟线程。

**钉住**（pinning）：某些操作导致无法卸载，载体被占住，其他虚拟线程等待。

| 场景 | 是否钉住 |
|------|----------|
| `Thread.sleep()` / IO 阻塞 | 否（卸载） |
| `synchronized` 内阻塞 | **JDK 24 以前钉住**，JDK 24+（JEP 491）已缓解 |
| JNI / native 方法阻塞 | 仍会钉住 |
| 长时间占用 `synchronized` 临界区 | 仍需注意 |

诊断钉住：

```bash
java -Djdk.tracePinnedThreads=full ...
```

建议：优先 `java.util.concurrent.locks.ReentrantLock`，少用 `synchronized` + 阻塞。

## 5. 结构化并发 StructuredTaskScope（Java 21 预览 → **JDK 25 仍为预览**）

把一组子任务的生命周期约束在 try-with-resources 作用域内：**作用域退出前必须全部结束**，消除任务泄漏与失控线程。

> **重要（JDK 25.0.2 实测）**：`java.util.concurrent.StructuredTaskScope` 在 JDK 25 中**仍是预览 API**，
> 编译与运行都必须加 `--enable-preview`。因此本模块的 `StructuredConcurrencyDemo.java`
> 用「虚拟线程 + CompletableFuture」实现了等价语义，保证 `./run.sh` 在原生 JDK 25 上直接跑通。
> 下面是预览 API 的完整写法，供你加 `--enable-preview` 时参考。

**API 形态（Java 25 定稿为 `Joiner`，不再有 `ShutdownOnFailure` 内部类）**：

```java
// 汇总全部成功子任务
try (var scope = StructuredTaskScope.open(Joiner.<String>allSuccessfulOrThrow())) {
    Subtask<String> u = scope.fork(() -> fetch("user"));
    Subtask<String> o = scope.fork(() -> fetch("order"));
    Stream<Subtask<String>> all = scope.join();   // 任一失败则抛
    all.forEach(s -> System.out.println(s.get()));
}

// shutdown-on-failure：任一失败 → 取消其余并在 join() 抛出
try (var scope = StructuredTaskScope.open(Joiner.<String>awaitAllSuccessfulOrThrow())) {
    scope.fork(() -> fetch("user"));
    scope.fork(() -> fetch("bad"));
    scope.join();
}

// 取第一个成功结果（类似 ExecutorCompletionService / invokeAny）
try (var scope = StructuredTaskScope.open(Joiner.<String>anySuccessfulResultOrThrow())) {
    scope.fork(() -> slow());
    scope.fork(() -> fast());
    String first = scope.join();
}
```

编译运行预览版：

```bash
javac --enable-preview --release 25 ...
java  --enable-preview ...
```

对比 `Future` 手写：无需 `shutdown()`、异常自动传播、任务一定结束。

**运行时替代（无需 preview，本模块采用）**：

```java
try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
    Future<String> u = exec.submit(() -> fetch("user"));
    Future<String> o = exec.submit(() -> fetch("order"));
    System.out.println(u.get() + o.get());
}
```

## 6. 作用域值 ScopedValue（Java 21 预览 → Java 25 正式）

在作用域内把值绑定给线程（含子线程），作用域结束自动失效：

```java
private static final ScopedValue<String> USER = ScopedValue.newInstance();

ScopedValue.where(USER, "alice").run(() -> {
    System.out.println(USER.get());   // alice
    inner();                          // 嵌套调用也能读
});
USER.isBound();   // 出作用域后 false
USER.orElse("匿名");

// 多值链式
ScopedValue.where(USER, "bob").where(TRACE_ID, "trace-1").run(() -> { ... });

// 带返回值
Integer len = ScopedValue.where(USER, "dave").call(() -> USER.get().length());
```

**线程继承要小心**：ScopedValue 的绑定**只在结构化并发（`StructuredTaskScope.fork`）的子任务中被继承**。
普通 `Thread.start()` / `Thread.ofVirtual().start()` 出去的线程**读不到**绑定（实测验证）：

```java
ScopedValue.where(USER, "carol").run(() -> {
    Thread.ofVirtual().start(() ->
        USER.orElse("读不到")   // 普通线程不继承
    );
});
// 而 StructuredTaskScope.fork 的子任务可以读到 USER
```

### ScopedValue vs ThreadLocal

| | ThreadLocal | ScopedValue |
|---|---|---|
| 可变性 | 可 set 任意值 | 绑定后不可变 |
| 清理 | 必须手动 `remove()`，否则泄漏 | 自动失效 |
| 子任务继承 | 默认不继承 | `StructuredTaskScope.fork` 子任务继承 |
| 线程池安全 | 复用时易串数据 | 天然安全 |

**优先 ScopedValue**，少用 ThreadLocal。

## 7. 并发陷阱与最佳实践

**陷阱**：

1. 非原子自增 `counter++` → 丢失更新（虚拟线程放大了并发度，更容易暴露）
2. `ThreadLocal` 用完不 `remove()` → 线程池场景泄漏
3. CPU 密集型任务误用虚拟线程 → 无收益甚至更慢
4. 用 `sleep` 忙等共享状态 → 改用 `CountDownLatch` / `CompletableFuture` / 结构化并发
5. 在虚拟线程里池化虚拟线程 → 死锁风险

**最佳实践**：

```java
// 1. 虚拟线程随建随用
try (var exec = Executors.newVirtualThreadPerTaskExecutor()) { ... }

// 2. CPU 密集用固定平台线程池
Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

// 3. 共享状态用原子类 / 并发集合
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
AtomicInteger counter = new AtomicInteger();

// 4. 上下文传值用 ScopedValue
// 5. 子任务生命周期用 StructuredTaskScope
```

## 运行本模块

**VS Code**：打开 `src/com/learn/vthreads/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `VirtualThreadBasics.java` | 三种创建方式、10 万虚拟线程实验 |
| `ThreadPerTaskExecutorDemo.java` | 每任务一线程 vs 线程池对比 |
| `MountingVsPinning.java` | 挂载/卸载、synchronized 钉住说明 |
| `StructuredConcurrencyDemo.java` | 预览 API 说明 + CompletableFuture 等价实现 |
| `ScopedValuesDemo.java` | 绑定、继承边界、与 ThreadLocal 对比 |
| `ConcurrencyPitfalls.java` | 常见陷阱与最佳实践清单 |

## 易错点

1. **虚拟线程不要池化**——创建几乎免费，池化反而引入排队与错乱
2. CPU 密集型不要用虚拟线程，用固定平台线程池
3. `synchronized` 内阻塞在 **JDK 24 以前**会钉住载体线程，升级到 24+ 或改 `ReentrantLock`
4. `ThreadLocal` 用完必须 `remove()`，虚拟线程虽不复用但仍建议改 `ScopedValue`
5. `ScopedValue` 绑定后不可变，想改值必须在新的 `where(...)` 作用域里覆盖
6. `StructuredTaskScope` 在 **JDK 25 仍是预览 API**，必须加 `--enable-preview`；本模块运行示例已改用 CompletableFuture 等价实现
7. `ScopedValue` 绑定只被 `StructuredTaskScope.fork` 子任务继承，普通 `Thread.start()` 读不到
8. `StructuredTaskScope` 是 try-with-resources，退出作用域前子任务一定结束——别把 scope 传出方法外
9. 结构化并发中子任务抛异常会让 `join()` 抛出，记得 catch 或声明 `throws`
10. 虚拟线程上 `Thread.sleep` 便宜，但仍要避免无意义的忙等轮询
