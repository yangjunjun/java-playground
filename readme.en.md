# Java Fundamentals Practice Repository

This repository is for systematically learning Java fundamentals (covering Java 8 → Java 25 LTS). Each subfolder focuses on one topic and contains explanatory documentation along with runnable practice code.

## Directory Convention

```
<topic>/
├── README.md     # Explanation and key points for the topic
├── run.sh        # One-command compile & run from the command line (optional)
└── src/          # Practice source code
```

## Topic Index

| Folder | Topic | Key Contents |
|--------|--------|----------|
| [01-basics/](01-basics/) | Language Basics | Variables, data types, operators, control flow |
| [02-string/](02-string/) | Strings and Text | String, StringBuilder, text blocks, formatting |
| [03-array/](03-array/) | Arrays | Declaration, copying, multidimensional arrays, Arrays utility class |
| [04-wrapper/](04-wrapper/) | Wrapper Classes | Autoboxing/unboxing, Integer cache pitfalls |
| [05-oop/](05-oop/) | Object-Oriented Programming | Encapsulation, inheritance, polymorphism, abstract classes, interfaces, inner classes |
| [06-exception/](06-exception/) | Exception Handling | Checked/unchecked exceptions, try-with-resources, custom exceptions |
| [07-generics/](07-generics/) | Generics | Wildcards, type erasure, bounded types |
| [08-enums/](08-enums/) | Enums | Constant-specific methods, EnumSet/EnumMap, strategy enums |
| [09-annotation/](09-annotation/) | Annotations | Built-in/custom/meta-annotations, reflective reading, repeatable annotations |
| [10-collections/](10-collections/) | Collections Framework | List/Set/Map/Queue, immutable factories, Sequenced Collections |
| [11-lambda/](11-lambda/) | Lambda and Functional Interfaces | Method references, java.util.function, function composition |
| [12-stream/](12-stream/) | Stream API | Intermediate/terminal operations, Collectors, parallel streams |
| [13-optional/](13-optional/) | Optional | Creation, map/flatMap, the orElse family |
| [14-datetime/](14-datetime/) | Date and Time | LocalDate/LocalDateTime/Instant, time zones, formatting |
| [15-io/](15-io/) | IO and NIO | Byte/character streams, Path/Files, channels and buffers |
| [16-concurrency/](16-concurrency/) | Concurrency | Threads, locks, thread pools, CompletableFuture |
| [17-reflection/](17-reflection/) | Reflection | Class, dynamic invocation, dynamic proxy |
| [18-regex/](18-regex/) | Regular Expressions | Pattern/Matcher, groups, replacement |
| [19-jdbc/](19-jdbc/) | JDBC | Connections, PreparedStatement, transactions, batch processing |
| [20-network/](20-network/) | Networking | Socket/TCP, URL, HttpClient |
| [21-crypto/](21-crypto/) | Cryptography and Security | Digests, symmetric/asymmetric encryption, digital signatures |
| [22-i18n/](22-i18n/) | Internationalization | Locale, ResourceBundle, message and number formats |
| [23-modules/](23-modules/) | Module System | JPMS, ServiceLoader, module path |
| [24-modern-syntax/](24-modern-syntax/) | Modern Syntax | var, switch expressions, records, sealed classes, pattern matching |
| [25-virtual-threads/](25-virtual-threads/) | Virtual Threads | Virtual threads, structured concurrency, ScopedValue |

## Environment Requirements

- **JDK 25 LTS** (this repository is written and verified with Java 25 and stays compatible with Java 8 syntax; features marked `(Java X+)` require the corresponding version)
- Recommended install via [SDKMAN!](https://sdkman.io/): `sdk install java 25.0.2-tem && sdk default java 25.0.2-tem`
- VS Code + Extension Pack for Java
- Optional: Bash (for `run.sh`)

## How to Run

### Option 1: VS Code (recommended)

1. Open **the root directory of this repository** in VS Code
2. Open `<topic>/src/.../Main.java`
3. Click the **Run** / **Debug** button above the `main` method

You can also press **F5** and select `Run <Topic> Main` from the launch configuration list.

### Option 2: Command line

```bash
cd <topic>
./run.sh
```

Or run every topic at once:

```bash
./scripts/check-all.sh
```

## How to Add a New Topic

1. Create a new folder named `NN-english-name` (two-digit sequence number + lowercase English, e.g. `26-network-advanced`), with the number continuing after the existing topics
2. Create a `README.md` explaining the concepts, syntax, and common pitfalls
3. Put the practice code under `src/com/learn/<topic>/`, providing a `Main` entry class with `public static void main`
4. Provide a `run.sh` for convenient command-line runs
5. Append `"<topic>/src"` to `java.project.sourcePaths` in `.vscode/settings.json`
6. Add the corresponding launch configuration in `.vscode/launch.json`
7. Update the topic table in this file
