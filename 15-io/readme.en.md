# IO and NIO (java.io / java.nio.file)

Java I/O comes in two generations: the early `java.io` (stream-based Stream API) and `java.nio.file` (path-based NIO.2), introduced in Java 7. For everyday development, prefer `java.nio.file.Files` + `Path`; reach for the classic streams only when you need byte-by-byte / character-by-character processing or must wrap an existing stream. This module covers the commonly used I/O capabilities from Java 8 to Java 25.

## 1. Byte Streams and Character Streams (Java 8+)

Two families, by unit of processing:

| Type | Abstract base classes | Unit | Typical use |
|------|-----------------------|------|-------------|
| Byte streams | `InputStream` / `OutputStream` | Bytes (8 bits) | Images, audio, arbitrary binary data |
| Character streams | `Reader` / `Writer` | Characters (16 bits) | Text files, logs |

```java
// Byte streams: FileInputStream / FileOutputStream
try (InputStream in = new FileInputStream("a.bin");
     OutputStream out = new FileOutputStream("b.bin")) {
    in.transferTo(out); // Java 9+, copy the whole thing
}

// Character streams: FileReader / FileWriter (use the platform default encoding — a common pitfall)
try (Reader reader = new FileReader("a.txt");
     Writer writer = new FileWriter("b.txt")) {
    writer.write(reader.read());
}
```

## 2. Character Encoding and BufferedReader / PrintWriter (Java 8+)

`FileReader`/`FileWriter` use the **platform default encoding**, which turns into mojibake across platforms. Specify `StandardCharsets.UTF_8` explicitly:

```java
try (Reader reader = Files.newBufferedReader(Path.of("a.txt"), StandardCharsets.UTF_8);
     Writer writer = Files.newBufferedWriter(Path.of("b.txt"), StandardCharsets.UTF_8)) {
    writer.write("Chinese content");
}

// The usual way to read line by line
try (BufferedReader br = new BufferedReader(new FileReader("a.txt", StandardCharsets.UTF_8))) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
}
```

`PrintWriter` offers `println`/`printf` and can enable auto-flushing, making it a good fit for writing log text.

## 3. File Class vs. java.nio.file.Files (Java 8+)

Old `java.io.File`: exists/mkdirs/delete/getAbsolutePath; the new API: `Path` + `Files` static utility methods.

```java
File dir = new File("data/logs");
dir.mkdirs();
System.out.println(dir.exists() + " " + dir.getAbsolutePath());

// New API (recommended): Files.readAllLines / copy / move / delete
Files.writeString(Path.of("data/a.txt"), "content", StandardCharsets.UTF_8); // Java 11+
List<String> lines = Files.readAllLines(Path.of("data/a.txt"), StandardCharsets.UTF_8);
```

## 4. Path / Paths.get and Common Files Methods (Java 8+; `Path.of` is Java 11+)

```java
Path p = Path.of("data", "a.txt");          // Java 11+; on Java 8 use Paths.get(...)
System.out.println(p.getFileName() + " " + p.toAbsolutePath());
System.out.println(Files.exists(p));
Files.copy(p, Path.of("data/b.txt"), StandardCopyOption.REPLACE_EXISTING);
Files.move(p, Path.of("data/c.txt"), StandardCopyOption.REPLACE_EXISTING);
```

Relative paths resolve against `user.dir` (the working directory); absolute paths start from the root. When debugging "file not found", confirm the current working directory first.

## 5. Directory Traversal: Files.walk / walkFileTree (Java 8+)

```java
try (Stream<Path> walk = Files.walk(Path.of("data"))) {
    walk.filter(Files::isRegularFile).forEach(System.out::println);
}

// walkFileTree: visitor callbacks, can terminate early
Files.walkFileTree(Path.of("data"), new SimpleFileVisitor<>() {
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        System.out.println("file: " + file);
        return FileVisitResult.CONTINUE;
    }
});
```

## 6. ByteBuffer and FileChannel (Java 8+; `transferTo` is Java 11+)

NIO channels + buffers: buffer-oriented bulk I/O that copies large files efficiently with `transferTo`/`transferFrom`.

```java
try (FileChannel in = FileChannel.open(Path.of("a.txt"), StandardOpenOption.READ);
     FileChannel out = FileChannel.open(Path.of("b.txt"),
             StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
    in.transferTo(0, in.size(), out); // Java 11+ concise form; or loop manually with ByteBuffer
}
```

## 7. try-with-resources (Java 7+) and Resource Leaks

Every `Closeable`/`AutoCloseable` should go in a try-with-resources block:

```java
try (InputStream in = new FileInputStream("a.txt")) {
    in.readAllBytes();
} // closed automatically whether or not an exception occurs
```

Forgetting to close streams is the most common resource leak. Many `Files` methods close automatically, but streams you create manually must be closed by you.

## 8. Object Serialization (Brief)

`ObjectOutputStream`/`ObjectInputStream` write objects as byte streams; serialized classes must implement `Serializable`. Beware: serialization has version-compatibility issues and many security hazards — for new projects, prefer explicit formats such as JSON.

```java
try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
    oos.writeObject(list);
}
```

## 9. Running This Module

**VS Code**: open `src/com/learn/io/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|----------|
| `Main.java` | Main entry point, runs all examples in order |
| `ByteStreamDemo.java` | Byte stream hierarchy and read/write |
| `CharStreamDemo.java` | Character streams, encoding, and buffered I/O |
| `FileClassDemo.java` | File class: exists/mkdirs/delete |
| `NioFilesDemo.java` | Common java.nio.file.Files methods |
| `PathWalkDemo.java` | Path/Paths and directory traversal |
| `BufferChannelDemo.java` | ByteBuffer and FileChannel |
| `IoTryWithResources.java` | try-with-resources and closing resources |
| `ObjectStreamBrief.java` | Brief object serialization example |

## Common Pitfalls

1. Not closing streams causes resource leaks — always use try-with-resources
2. `FileReader`/`FileWriter` depend on the platform default encoding; Chinese text may turn to mojibake on different OSes — specify UTF-8 explicitly
3. Relative paths resolve against the working directory `user.dir`, which may differ between the IDE and the command line
4. `delete()` silently fails on a non-empty directory (returns false); `Files.delete` throws an exception that explains why
5. The Stream returned by `Files.walk` must be closed (try-with-resources), or file handles leak
6. `File.exists()` also returns false when permissions are insufficient — be careful not to misread that as "does not exist"
