# IO 与 NIO（java.io / java.nio.file）

Java 的 IO 分为两代：早期的 `java.io`（基于流的 Stream API）和 Java 7 引入的 `java.nio.file`（基于路径的 NIO.2）。日常开发推荐优先用 `java.nio.file.Files` + `Path`，只有在需要逐字节/逐字符处理、包装已有流时才用经典 Stream。本模块覆盖 Java 8 到 Java 25 的常用 IO 能力。

## 1. 字节流与字符流（Java 8+）

按处理单位分两类：

| 类型 | 抽象基类 | 处理单位 | 典型场景 |
|------|----------|----------|----------|
| 字节流 | `InputStream` / `OutputStream` | 字节（8 bit） | 图片、音频、任意二进制 |
| 字符流 | `Reader` / `Writer` | 字符（16 bit） | 文本文件、日志 |

```java
// 字节流：FileInputStream / FileOutputStream
try (InputStream in = new FileInputStream("a.bin");
     OutputStream out = new FileOutputStream("b.bin")) {
    in.transferTo(out); // Java 9+，整段拷贝
}

// 字符流：FileReader / FileWriter（使用平台默认编码，易踩坑）
try (Reader reader = new FileReader("a.txt");
     Writer writer = new FileWriter("b.txt")) {
    writer.write(reader.read());
}
```

## 2. 字符编码与 BufferedReader / PrintWriter（Java 8+）

`FileReader`/`FileWriter` 使用**平台默认编码**，跨平台会乱码。显式指定 `StandardCharsets.UTF_8`：

```java
try (Reader reader = Files.newBufferedReader(Path.of("a.txt"), StandardCharsets.UTF_8);
     Writer writer = Files.newBufferedWriter(Path.of("b.txt"), StandardCharsets.UTF_8)) {
    writer.write("中文内容");
}

// 按行读取的常用姿势
try (BufferedReader br = new BufferedReader(new FileReader("a.txt", StandardCharsets.UTF_8))) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
}
```

`PrintWriter` 提供 `println`/`printf`，可开启自动刷新，适合写日志文本。

## 3. File 类 vs java.nio.file.Files（Java 8+）

旧 `java.io.File`：exists/mkdirs/delete/getAbsolutePath；新 API：`Path` + `Files` 静态工具方法。

```java
File dir = new File("data/logs");
dir.mkdirs();
System.out.println(dir.exists() + " " + dir.getAbsolutePath());

// 新 API（推荐）：Files.readAllLines / copy / move / delete
Files.writeString(Path.of("data/a.txt"), "内容", StandardCharsets.UTF_8); // Java 11+
List<String> lines = Files.readAllLines(Path.of("data/a.txt"), StandardCharsets.UTF_8);
```

## 4. Path / Paths.get 与 Files 常用方法（Java 8+，`Path.of` 为 Java 11+）

```java
Path p = Path.of("data", "a.txt");          // Java 11+；Java 8 用 Paths.get(...)
System.out.println(p.getFileName() + " " + p.toAbsolutePath());
System.out.println(Files.exists(p));
Files.copy(p, Path.of("data/b.txt"), StandardCopyOption.REPLACE_EXISTING);
Files.move(p, Path.of("data/c.txt"), StandardCopyOption.REPLACE_EXISTING);
```

相对路径基于 `user.dir`（工作目录），绝对路径从根开始——排查“文件找不到”先确认当前工作目录。

## 5. 目录遍历：Files.walk / walkFileTree（Java 8+）

```java
try (Stream<Path> walk = Files.walk(Path.of("data"))) {
    walk.filter(Files::isRegularFile).forEach(System.out::println);
}

// walkFileTree：带访问器回调，可提前终止
Files.walkFileTree(Path.of("data"), new SimpleFileVisitor<>() {
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        System.out.println("文件: " + file);
        return FileVisitResult.CONTINUE;
    }
});
```

## 6. ByteBuffer 与 FileChannel（Java 8+，`transferTo` Java 11+）

NIO 通道 + 缓冲区：面向缓冲区的批量 IO，可与 `transferTo`/`transferFrom` 高效拷贝大文件。

```java
try (FileChannel in = FileChannel.open(Path.of("a.txt"), StandardOpenOption.READ);
     FileChannel out = FileChannel.open(Path.of("b.txt"),
             StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
    in.transferTo(0, in.size(), out); // Java 11+ 简洁版；或手动 ByteBuffer 循环
}
```

## 7. try-with-resources（Java 7+）与资源泄漏

所有 `Closeable`/`AutoCloseable` 都应放进 try-with-resources：

```java
try (InputStream in = new FileInputStream("a.txt")) {
    in.readAllBytes();
} // 无论是否异常都会自动 close
```

忘关流是最常见的资源泄漏；`Files` 的很多方法会自动关闭，但手工创建的流必须自己关。

## 8. 对象序列化（简要）

`ObjectOutputStream`/`ObjectInputStream` 把对象写成字节流；被序列化的类需实现 `Serializable`。注意：序列化版本兼容、安全隐患多，新项目优先 JSON 等显式格式。

```java
try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
    oos.writeObject(list);
}
```

## 9. 运行本模块

**VS Code**：打开 `src/com/learn/io/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `ByteStreamDemo.java` | 字节流层次与读写 |
| `CharStreamDemo.java` | 字符流、编码与缓冲读写 |
| `FileClassDemo.java` | File 类的 exists/mkdirs/delete |
| `NioFilesDemo.java` | java.nio.file.Files 常用方法 |
| `PathWalkDemo.java` | Path/Paths 与目录遍历 |
| `BufferChannelDemo.java` | ByteBuffer 与 FileChannel |
| `IoTryWithResources.java` | try-with-resources 与资源关闭 |
| `ObjectStreamBrief.java` | 对象序列化简要示例 |

## 易错点

1. 不关闭流导致资源泄漏——一律用 try-with-resources
2. `FileReader`/`FileWriter` 依赖平台默认编码，中文在不同 OS 上可能乱码；显式指定 UTF-8
3. 相对路径基于工作目录 `user.dir`，IDE 与命令行运行的目录可能不同
4. `delete()` 删除非空目录会失败且不报错（返回 false）；`Files.delete` 会抛异常说明原因
5. `Files.walk` 返回的 Stream 必须 close（try-with-resources），否则文件句柄泄漏
6. `File.exists()` 对权限不足也返回 false，判断“不存在”要小心误判
