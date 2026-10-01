# 反射（Reflection）

反射让程序在**运行时**探知类的结构（字段、方法、构造器、注解），并动态创建对象、调用方法。它是框架的基石（Spring IoC、Jackson、JUnit、MyBatis 都依赖反射），但也带来性能与封装性代价。本模块覆盖 Java 8 到 Java 25 的常用反射能力。

## 1. 获取 Class 对象（Java 8+）

三种等价方式：

```java
Class<?> c1 = Class.forName("com.learn.reflection.ClassBasics$Demo"); // 全限定名，可能抛 ClassNotFoundException
Class<?> c2 = ClassBasics.Demo.class;                                  // 编译期确定，最安全
Class<?> c3 = obj.getClass();                                          // 运行时由实例得到
```

常用方法：`getName()`、`getSimpleName()`、`getSuperclass()`、`getInterfaces()`、`isInterface()`、`getModifiers()`。

## 2. 创建对象：newInstance vs 构造器（Java 8+）

```java
// 过时写法（Java 9 标记 @Deprecated）：只能调无参构造，异常被包装
Object o1 = cls.newInstance();

// 推荐写法（Java 9+ 起正式推荐）：
Object o2 = cls.getDeclaredConstructor().newInstance();
```

`getDeclaredConstructor(参数类型...)` 可以拿到任意可见性的构造器（含 private），再配合 `setAccessible(true)` 调用。

## 3. 字段访问（Java 8+）

```java
Field f = cls.getDeclaredField("name");
f.setAccessible(true);   // 突破 private 访问限制
f.set(obj, "张三");
Object v = f.get(obj);
```

**模块化注意（Java 9+）**：跨模块反射访问 JDK 内部或未 `opens` 的包会抛 `InaccessibleObjectException`。自类反射可直接 `setAccessible(true)`；JDK 内部需要 `--add-opens`。

## 4. 方法调用（Java 8+）

```java
Method m = cls.getDeclaredMethod("greet", String.class); // 方法名 + 参数类型
m.setAccessible(true);
Object result = m.invoke(obj, "Java");   // 目标对象 + 实参
```

`invoke` 若目标方法抛异常，会被包装成 `InvocationTargetException`，真实原因在 `getCause()`。

## 5. 泛型信息（Java 8+）

泛型在运行时大部分被擦除，但**签名中的泛型**保留在 `getGenericSuperclass()` 等方法上：

```java
class StringRepo extends Repo<String> {}
Type superType = StringRepo.class.getGenericSuperclass();
// 用 ParameterizedType 取出实际类型参数
```

## 6. 运行时注解（Java 8+）

```java
if (cls.isAnnotationPresent(MyAnno.class)) {
    MyAnno a = cls.getAnnotation(MyAnno.class);
    System.out.println(a.value());
}
```

前提是注解 `@Retention(RetentionPolicy.RUNTIME)`，详见「注解」模块。

## 7. 动态代理（Java 8+）

`java.lang.reflect.Proxy` + `InvocationHandler`：运行时生成接口实现，统一增强方法（AOP 雏形）。

```java
Service proxy = (Service) Proxy.newProxyInstance(
        Service.class.getClassLoader(),
        new Class<?>[] { Service.class },
        (p, method, args) -> {
            System.out.println("调用前: " + method.getName());
            return method.invoke(target, args);
        });
```

注意：`InvocationTargetException` 要 `getCause()` 解包；`Proxy` 只能代理接口（代理类需第三方库如 CGLIB）。

## 8. 性能与安全、常见异常

- 性能：反射调用比直接调用慢一个数量级，热点路径慎用；`setAccessible(true)` 也会有安全检查开销。
- 安全：破坏封装，暴露私有实现；框架中应配合权限/模块系统约束。

| 异常 | 触发原因 |
|------|----------|
| `ClassNotFoundException` | `Class.forName` 类名写错/不在 classpath |
| `NoSuchMethodException` | `getMethod`/`getDeclaredMethod` 参数类型不匹配 |
| `NoSuchFieldException` | 字段名不存在 |
| `IllegalAccessException` | 无访问权限且未 `setAccessible(true)` |
| `InvocationTargetException` | 被调用方法内部抛异常（用 `getCause()` 解包） |
| `InaccessibleObjectException`（Java 9+） | 模块未 `opens` 就强行 setAccessible |

## 9. 运行本模块

**VS Code**：打开 `src/com/learn/reflection/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `ClassBasics.java` | Class 对象获取与类信息 |
| `Instantiation.java` | 反射创建对象 |
| `FieldAccess.java` | 字段读写与 setAccessible |
| `MethodInvoke.java` | 方法查找与 invoke |
| `ConstructorAccess.java` | 构造器反射与私有构造 |
| `AnnotationReflect.java` | 运行时读取注解 |
| `ProxyReflect.java` | 动态代理 Proxy + InvocationHandler |
| `ReflectPitfalls.java` | 常见异常与性能/模块注意 |

## 易错点

1. `Class.forName("Demo")` 忘写包名（全限定名 `com.learn.xxx.Demo`）导致 `ClassNotFoundException`
2. `getDeclaredMethod("greet", String.class)` 参数类型漏写/写错，抛 `NoSuchMethodException`
3. 反射调用 private 成员前忘记 `setAccessible(true)`，抛 `IllegalAccessException`
4. `invoke` 抛 `InvocationTargetException` 时直接打印 e，看不到真实堆栈——要 `e.getCause()`
5. `cls.newInstance()` 已过时且无法处理有参构造/受检异常，改用 `getDeclaredConstructor().newInstance()`
6. Java 9+ 模块系统下对 JDK 内部强反射会 `InaccessibleObjectException`
7. 动态代理只能代理**接口**，类需要 CGLIB 等第三方方案
