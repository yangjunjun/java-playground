# 注解（Annotation）

注解是 Java 5 引入的元数据机制：它为代码（类、方法、字段、参数等）附加说明信息，可被编译器检查，也可在运行时通过反射读取。框架（Spring、JUnit、Jackson）大量依赖注解做配置与装配。

## 1. 内置注解

| 注解 | 作用 |
|------|------|
| `@Override` | 标记方法重写父类/接口方法，写错签名时编译报错 |
| `@Deprecated` | 标记过时 API，使用时编译器给出警告 |
| `@SuppressWarnings` | 抑制指定类型的编译警告（如 `"unused"`、`"unchecked"`） |
| `@FunctionalInterface` | 标记函数式接口（Java 8），保证接口只有一个抽象方法 |

## 2. 自定义注解

使用 `@interface` 定义，成员以“方法”形式声明，可带默认值：

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyLabel {
    String value() default "";
    int priority() default 0;
}
```

使用时用 `名称 = 值` 形式；只有 `value` 且其余都有默认值时可省略 `value =`：

```java
@MyLabel("登录")
public void login() {}

@MyLabel(value = "下单", priority = 2)
public void order() {}
```

## 3. 元注解

元注解是“注解的注解”，用来约束自定义注解：

| 元注解 | 作用 |
|--------|------|
| `@Target` | 注解能标在哪些元素上（`TYPE`、`METHOD`、`FIELD`、`PARAMETER` 等） |
| `@Retention` | 注解保留到哪个阶段：`SOURCE`（仅源码）/ `CLASS`（进字节码）/ `RUNTIME`（运行时可反射读取） |
| `@Documented` | 生成 Javadoc 时包含该注解 |
| `@Inherited` | 子类自动继承父类上的该类注解 |

**重点**：需要运行时反射读取时，`@Retention` 必须是 `RUNTIME`，否则 `getAnnotation()` 拿不到。

## 4. 反射读取注解

```java
if (obj.isAnnotationPresent(MyLabel.class)) {
    MyLabel label = obj.getAnnotation(MyLabel.class);
    System.out.println(label.value());
}
```

## 5. 重复注解（Java 8）

同一元素上可多次使用同一注解，需配合容器注解：

```java
@Repeatable(Tags.class)
@interface Tag { String value(); }

@interface Tags { Tag[] value(); }
```

## 6. 运行本模块

**VS Code**：打开 `src/com/learn/annotation/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `BuiltinAnnotations.java` | 内置注解用法 |
| `CustomAnnotation.java` | 自定义注解定义与使用 |
| `MetaAnnotation.java` | 元注解约束 |
| `AnnotationReflection.java` | 运行时反射读取注解 |
| `RepeatableAnnotation.java` | 重复注解 |

## 易错点

1. `@Retention` 写成 `SOURCE`/`CLASS`，反射读不到
2. 自定义注解元素写成有参方法会编译失败——只能是无参的“成员”
3. `@Inherited` 只对类上的注解生效，方法/字段上的注解不会被继承
4. 重复注解的容器注解 `value()` 返回类型必须是注解数组
