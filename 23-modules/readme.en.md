# Java Platform Module System (JPMS)

JPMS is the modularization mechanism introduced in Java 9 (JEP 261), also known as Project Jigsaw. It aggregates packages into modules, enforcing declared dependencies and visibility at both compile time and run time, and fixes the class path's broken encapsulation, invisible dependencies, bloated images, and related problems. This module contains a runnable single-module example as well as a **true two-module compile-and-run** example under `multi/`.

## 1. Why JPMS Is Needed

| Problem (class path) | JPMS's answer |
|---|---|
| Any public class can be called via reflection | Only `exports`-ed packages are visible externally |
| Dependencies live in POM/Gradle; not checked at run time | `requires` is checked at both compile time and startup |
| Multiple versions of the same jar coexist; behavior is indeterminate | The module graph is resolved once at startup; conflicts fail fast |
| Bloated runtime images (the whole JDK) | `jlink` packages only the modules you use |

Three major goals: **strong encapsulation**, **reliable configuration**, and a **scalable platform**.

## 2. Module Declaration: module-info.java

Each module root has a `module-info.java`:

```java
// greet module: exports the com.learn.greet package
module greet {
    exports com.learn.greet;
}

// app module: depends on greet
module app {
    requires greet;
    uses com.learn.modules.GreetingService;      // declare that we consume this service
}
```

## 3. Key Directives

| Directive | Meaning |
|------|------|
| `requires` | Depends on another module (`requires transitive` for transitive deps, `requires static` for optional deps) |
| `exports` | Exports a package to other modules (visible at compile time) |
| `opens` | Opens a package to reflection (`setAccessible` allowed at run time) |
| `exports ... to` | Exports only to specific modules (qualified export) |
| `provides ... with ...` | Declares a service implementation (paired with `uses`) |
| `uses` | Declares that we consume a service through `ServiceLoader` |

```java
module app {
    requires transitive greet;                      // transitive dependency
    exports com.learn.app.api;                      // export
    opens com.learn.app.model to spring.core;       // reflection only for spring.core
    provides GreetingService with EnglishGreeting;  // provide a service implementation
}
```

## 4. --module-path vs -classpath

```bash
# old: class path — everything lands in the unnamed module, encapsulation wide open
javac -classpath lib/* -d out src/Main.java
java  -classpath out:lib/* Main

# new: module path — every jar/directory is a named module
javac --module-path lib -d out $(find src -name '*.java')
java  --module-path out -m app/main.Main
```

| | `-classpath` | `--module-path` |
|---|---|---|
| Module identity | All unnamed | Named modules |
| Encapsulation | Broken | Enforced |
| Dependency checking | None | Compile time + startup |
| Reflection | Nearly unrestricted | Requires `opens` |

**Automatic modules**: a plain jar on the `--module-path` becomes an automatic module; its name is
derived from the jar filename (or `Automatic-Module-Name`), with all packages exported automatically
(encapsulation is broken) — a transitional measure for migrating legacy jars, not the end goal.

## 5. ServiceLoader Service Discovery (Java 6+ / modularized in 9+)

An interface plus multiple implementations, discovered dynamically at run time — decoupling "service definition" from "service providing":

```java
// service interface
public interface GreetingService {
    String greet(String name);
}

// consumer (uses GreetingService in module-info)
ServiceLoader<GreetingService> loader = ServiceLoader.load(GreetingService.class);
for (GreetingService s : loader) {
    System.out.println(s.greet("module"));
}
```

**Class-path configuration** (the old style): in a `META-INF/services/com.learn.GreetingService` file, one fully-qualified implementation class name per line.

**Module configuration** (Java 9+):

```java
// provider module
module greeter.impl {
    requires greeter.api;
    provides GreetingService with EnglishGreeting, ChineseGreeting;
}

// consumer module
module app {
    requires greeter.api;
    uses GreetingService;
}
```

## 6. Runtime Introspection: Module / ModuleDescriptor

```java
Module m = String.class.getModule();
m.isNamed();                                  // is it a named module?
m.getDescriptor().exports();                  // list of exported packages
ModuleLayer.boot().modules();                 // all modules in the boot layer
m.isExported("java.lang", caller);            // exported to a given module?
m.getDescriptor().isOpen();                   // is it an open module?
```

On the class path at run time, the current module is the **unnamed module**: all packages are automatically exported and reflection is nearly unrestricted — this is why encapsulation is "broken" on the class path.

## 7. Multi-Module Compile Example (modules/multi/)

A real two-module project where the `greet` module is `requires`-ed by the `app` module:

```
modules/multi/
├── greet/
│   ├── module-info.java          # module greet { exports com.learn.greet; }
│   └── com/learn/greet/Greeter.java
├── app/
│   ├── module-info.java          # module app { requires greet; }
│   └── main/Main.java
└── run.sh                        # compile + run + --describe-module
```

Core commands:

```bash
# 1. compile greet (compile the dependency first)
javac -d out/greet $(find greet -name '*.java')

# 2. compile app (--module-path is required to requires greet)
javac --module-path out -d out/app $(find app -name '*.java')

# 3. run: -m module-name/fully-qualified-main-class
java --module-path out -m app/main.Main

# 4. inspect the module descriptor
java --module-path out --describe-module app
```

## 8. jlink: Trimming the Runtime (Concept)

```bash
# package only the modules you use into a slim runtime image (no full JDK needed)
jlink --module-path $JAVA_HOME/jmods:out \
      --add-modules app \
      --output my-image

./my-image/bin/java -m app/main.Main
```

The image can be as small as a few tens of MB, suitable for containers and desktop distribution. See the `JLinkNotes` notes for details (not demonstrated in this module because it requires the full jmods path).

## 9. Migration Advice and Reflection with --add-opens

**Migration path**:

1. **Get it running on the class path first** — don't rush to modules
2. Add `Automatic-Module-Name` to jars and put them on the `--module-path` as automatic modules
3. Add `module-info.java` gradually, starting with leaf modules (no dependencies)
4. For framework (Spring/Jackson) packages that need reflection, open them with `opens`

**Typical error when reflection is rejected**:

```
java.lang.reflect.InaccessibleObjectException:
  Unable to make field private final ... accessible:
  module java.base does not "opens java.lang" to module app
```

Fix (runtime flags, for emergencies):

```bash
java --add-opens java.base/java.lang=ALL-UNNAMED ...
java --add-opens java.base/java.util=app ...
```

**Advice**: `--add-opens` is a transitional measure; in the long run, switch to `opens` declarations or public APIs.

## 10. Beyond That (Java 23 ~ 25, for awareness only)

| Feature | Version | Notes |
|------|------|------|
| Module import declaration `import module java.base;` | Java 25 | Import all exported packages of a module at once |
| Compact source files and instance main | Java 25 | `void main()` in a single file, no class needed |
| Primitive type pattern matching | Java 25 **preview** | Requires `--enable-preview` |

## Running This Module

**VS Code**: open `src/com/learn/modules/Main.java` and click **Run** above `main`.

**Command line** (includes the multi-module example):

```bash
./run.sh
```

To run only the multi-module example:

```bash
cd multi && ./run.sh
```

## Practice Files

| File | Contents |
|------|------|
| `Main.java` | Main entry point; runs all examples in sequence |
| `ModuleBasics.java` | Module / ModuleDescriptor / ModuleLayer introspection |
| `PackageEncapsulationDemo.java` | Public types vs package export, reflection differences |
| `ServiceLoaderDemo.java` | Service interface + two providers + ServiceLoader |
| `META-INF/services/...` | Class-path service provider declaration (run.sh copies it into out/) |
| `ModulePathVsClasspath.java` | Comparing the two paths, automatic modules, boot-layer module list |
| `multi/greet/` | The greet module being depended on (exports com.learn.greet) |
| `multi/app/` | The app module that depends on greet (`-m app/main.Main`) |
| `multi/run.sh` | Two-module compile-and-run script |

## Common Pitfalls

1. `exports` controls **compile-time visibility**; `opens` controls **reflection access** — with only exports and no opens, reflective `setAccessible` is rejected
2. Missing `requires` → `package ... is not visible` at compile time; don't wait until run time to find out
3. Automatic modules export all packages automatically — encapsulation is effectively off; use them for migration only, don't rely on them long-term
4. On the class path everything is the unnamed module, and `Module.isExported` is always true for unnamed — don't misread that as "exported"
5. `module-info.java` must be at the **module root** (sibling of the packages); put it in the wrong place and the compiler won't find it
6. The implementation class in `provides ... with ...` must have a no-arg constructor (or a Provider factory)
7. `--add-opens` gets you out of a bind, but it hides encapsulation design problems; don't overuse it
8. `jlink` can only package **modular** jars; automatic modules and class-path jars cannot be used with jlink
9. Compile order: compile the depended-on module (greet) first, then the dependent (app), and always pass `--module-path`
