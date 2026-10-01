# 异常处理（Exception）

异常是 Java 报告和处理错误的机制：出错时抛出 `Throwable` 对象，由调用方按需捕获处理，避免错误沿调用栈默默扩散。本模块覆盖 Throwable 层次、受检/非受检异常、try-catch-finally、try-with-resources、自定义异常与处理原则。

## 1. Throwable 层次

```text
Throwable
├── Error                —— JVM 级严重错误（OutOfMemoryError、StackOverflowError）
└── Exception
    ├── 受检异常          —— IOException、SQLException 等，编译器强制处理
    └── RuntimeException —— 非受检：NPE、ISE、ClassCastException 等
```

`Error` 一般不要捕获（恢复不了）；`Exception` 及其子类是程序可处理的。

## 2. 受检异常 vs 非受检异常

| | 受检异常（Checked） | 非受检异常（Unchecked） |
|--|---------------------|-------------------------|
| 继承自 | `Exception`（不含 RuntimeException） | `RuntimeException` |
| 编译器 | **强制** try-catch 或 throws | 不强制 |
| 典型 | `IOException` | `NullPointerException` |
| 适用 | 调用方能合理恢复的场景（如文件不存在） | 编程错误 / 无法预期的失败 |

```java
static String read() throws IOException {   // 受检：必须声明或捕获
    throw new IOException("读取失败");
}

static int div(int a, int b) {              // 非受检：可不声明
    return a / b;
}
```

## 3. try-catch-finally

执行顺序：`try` → `catch`（有异常时）→ `finally`（总是执行，JVM 关闭等极端情况除外）。

```java
try {
    mayFail();
} catch (IOException e) {
    System.out.println(e.getMessage());
} finally {
    System.out.println("总是执行");  // 释放资源
}
```

**陷阱**：`finally` 中的 `return` 会覆盖 try/catch 的返回值，还会吞掉未捕获异常——永远不要在 finally 里 return。

## 4. 多重捕获（Java 7+）

```java
try {
    mayFail();
} catch (IllegalArgumentException | IllegalStateException e) {
    // 一个分支处理多种异常；e 隐式 final，不能再赋值
}
```

多个 catch 的顺序：**子类在前、父类在后**，否则编译错误。

## 5. try-with-resources（Java 7）

实现 `AutoCloseable` 的资源可在 try 头部声明，自动按**逆序**关闭：

```java
try (InputStream in = Files.newInputStream(path);
     OutputStream out = Files.newOutputStream(dst)) {
    in.transferTo(out);
}
// 此处 in / out 已自动关闭
```

Java 9+ 可直接引用已存在的 final 或等效 final 变量：

```java
MyResource r = new MyResource();
try (r) { r.work(); }
```

**被抑制异常**：若 try 块抛了异常，close() 也抛异常时，close 的异常不会覆盖主异常，可通过 `e.getSuppressed()` 拿到：

```java
catch (Exception e) {
    e.printStackTrace();
    for (Throwable s : e.getSuppressed()) {
        s.printStackTrace();
    }
}
```

## 6. 自定义异常

- 需要被调用方处理 → 继承 `Exception`（受检）。
- 属于编程错误/业务断言 → 继承 `RuntimeException`（非受检）。
- 异常可序列化，建议显式写 `serialVersionUID` 保持版本兼容。

```java
public class InvalidAgeException extends Exception {
    private static final long serialVersionUID = 1L;
    private final int age;
    public InvalidAgeException(int age) {
        super("非法年龄: " + age);
        this.age = age;
    }
}
```

## 7. 重抛与包装

低层异常信息对调用方无意义时，包装成高层异常并**保留原始 cause**：

```java
try {
    parse();
} catch (NumberFormatException e) {
    throw new IllegalStateException("配置解析失败", e); // cause 保留现场
}
```

打印堆栈：`e.printStackTrace()`，或用日志框架 `log.error("失败", e)`；`getStackTrace()` 可编程读取。

## 8. 常见异常速览

| 异常 | 典型原因 |
|------|----------|
| `NullPointerException` | 空引用解引用（Java 14+ 增强消息指出具体变量，可快速定位） |
| `ClassCastException` | 强转类型不匹配 |
| `IndexOutOfBoundsException` | 索引越界 |
| `NumberFormatException` | 字符串转数字失败 |
| `IllegalArgumentException` | 方法参数非法 |
| `ArithmeticException` | 算术错误（如除零） |
| `UnsupportedOperationException` | 调用未实现/不可变集合的修改方法 |

## 9. 最佳实践

1. **捕获具体异常**，避免 `catch (Exception e)` 一刀切。
2. **不吞异常**：要么处理，要么包装上抛，至少记日志。
3. `finally` 不要 `return`；资源释放交给 try-with-resources。
4. 不要用异常做常规流程控制（异常创建有开销，且掩盖逻辑）。
5. 保留 cause，别 `throw new XxxException(e.getMessage())` 丢掉根因。

## 10. 运行本模块

**VS Code**：打开 `src/com/learn/exception/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `TryCatchFinally.java` | 执行顺序与 finally 中 return 的陷阱 |
| `CheckedVsUnchecked.java` | Throwable 层次、受检/非受检 |
| `CustomException.java` | 自定义异常与 serialVersionUID |
| `TryWithResources.java` | 自动关闭与被抑制异常 |
| `MultiCatchAndFinally.java` | multi-catch、包装重抛、堆栈跟踪 |
| `ExceptionBestPractices.java` | 常见异常与处理原则 |

## 易错点

1. `finally` 里写 `return` → 覆盖返回值并吞掉异常
2. `catch (Exception e)` 吞掉所有异常后什么都不做 → 问题被隐藏
3. 多个 catch 写成父类在前、子类在后 → 编译错误（子类 catch 必须在前）
4. 自定义异常忘记 `serialVersionUID` → 跨版本反序列化告警/失败
5. 包装异常时丢掉 cause（只传 message）→ 丢失根因堆栈
6. 受检异常既不捕获也不 `throws` → 编译错误；把非受检也全部声明会让签名爆炸
