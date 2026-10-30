# Annotations

Annotations are the metadata mechanism introduced in Java 5: they attach descriptive information to code (classes, methods, fields, parameters, and so on), which can be checked by the compiler and read at runtime via reflection. Frameworks (Spring, JUnit, Jackson) rely heavily on annotations for configuration and wiring.

## 1. Built-in Annotations

| Annotation | Purpose |
|------|------|
| `@Override` | Marks a method that overrides a superclass/interface method; a wrong signature becomes a compile error |
| `@Deprecated` | Marks an obsolete API; the compiler warns on use |
| `@SuppressWarnings` | Suppresses compile warnings of the given kinds (e.g. `"unused"`, `"unchecked"`) |
| `@FunctionalInterface` | Marks a functional interface (Java 8), guaranteeing the interface has exactly one abstract method |

## 2. Custom Annotations

Define one with `@interface`; members are declared like methods and can have default values:

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyLabel {
    String value() default "";
    int priority() default 0;
}
```

At the use site, members are given as `name = value`; when there is only `value` and everything else has a default, `value =` can be omitted:

```java
@MyLabel("login")
public void login() {}

@MyLabel(value = "place order", priority = 2)
public void order() {}
```

## 3. Meta-Annotations

Meta-annotations are "annotations on annotations"; they constrain custom annotations:

| Meta-annotation | Purpose |
|--------|------|
| `@Target` | Which elements the annotation may be placed on (`TYPE`, `METHOD`, `FIELD`, `PARAMETER`, etc.) |
| `@Retention` | How long the annotation is retained: `SOURCE` (source only) / `CLASS` (into bytecode) / `RUNTIME` (readable via reflection at runtime) |
| `@Documented` | Includes the annotation in generated Javadoc |
| `@Inherited` | Subclasses automatically inherit class-level annotations from their superclass |

**Key point**: if you need to read the annotation via reflection at runtime, `@Retention` must be `RUNTIME`, otherwise `getAnnotation()` won't see it.

## 4. Reading Annotations via Reflection

```java
if (obj.isAnnotationPresent(MyLabel.class)) {
    MyLabel label = obj.getAnnotation(MyLabel.class);
    System.out.println(label.value());
}
```

## 5. Repeatable Annotations (Java 8)

The same annotation can be used multiple times on one element; a container annotation is required:

```java
@Repeatable(Tags.class)
@interface Tag { String value(); }

@interface Tags { Tag[] value(); }
```

## 6. Running This Module

**VS Code**: open `src/com/learn/annotation/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|------|
| `Main.java` | Main entry point; runs all examples in order |
| `BuiltinAnnotations.java` | Usage of built-in annotations |
| `CustomAnnotation.java` | Defining and using custom annotations |
| `MetaAnnotation.java` | Meta-annotation constraints |
| `AnnotationReflection.java` | Reading annotations via reflection at runtime |
| `RepeatableAnnotation.java` | Repeatable annotations |

## Common Pitfalls

1. Setting `@Retention` to `SOURCE`/`CLASS` → reflection can't read it
2. Declaring a custom annotation element as a method with parameters fails to compile — elements must be parameterless "members"
3. `@Inherited` only applies to class-level annotations; annotations on methods/fields are not inherited
4. The container annotation's `value()` for repeatable annotations must return an array of annotations
