# Lambda 与函数式接口（Lambda）

Lambda 表达式是 **Java 8** 引入的匿名函数写法，它把“行为”当作值传递，取代了大量匿名内部类样板代码。Lambda 的类型由**目标类型**（target typing）决定：它必须赋给一个“函数式接口”（只有一个抽象方法的接口）。配合 `java.util.function` 包与方法引用，可以写出简洁、可组合的声明式代码。

## 1. Lambda 语法与目标类型

基本语法：`(参数) -> 表达式` 或 `(参数) -> { 语句块; }`。

```java
Runnable r = () -> System.out.println("无参");
StringFunc upper = s -> s.toUpperCase();      // 单参可省略 ()，类型可推断
IntFunc square = x -> x * x;                  // 表达式体
IntFunc f = x -> { int a = Math.abs(x); return a + 1; }; // 块体要显式 return
BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b; // 多参必须加 ()
```

Lambda 本身没有类型，由赋值目标推断：

```java
names.sort((x, y) -> x.length() - y.length()); // 目标类型是 Comparator
```

## 2. @FunctionalInterface

只有一个抽象方法（SAM）的接口是**函数式接口**；`default`/`static` 方法不算。用 `@FunctionalInterface` 标注可让编译器校验。

```java
@FunctionalInterface
interface Calculator {
    int calc(int a, int b);
    default int inc(int x) { return calc(x, 1); }
    static int zero() { return 0; }
}
```

**重载歧义**：两个重载方法的参数都是不同函数式接口时，裸 lambda 无法推断目标类型，需显式转型：

```java
overloaded((Foo) s -> System.out.println(s)); // 编译错误：overloaded(s -> ...)
```

## 3. 方法引用四种形式

| 形式 | 写法 | 等价 lambda |
|------|------|-------------|
| 静态方法引用 | `ClassName::staticMethod` | `(a) -> ClassName.staticMethod(a)` |
| 无界实例方法引用 | `ClassName::instanceMethod` | `(a) -> a.instanceMethod()` |
| 有界实例方法引用 | `object::instanceMethod` | `(a) -> object.instanceMethod(a)` |
| 构造器引用 | `ClassName::new` | `(a) -> new ClassName(a)` |

```java
UnaryOperator<String> shout = MethodReferences::shout; // 静态
Function<String, String> upper = String::toUpperCase;  // 无界实例
Function<String, String> bound = prefix::concat;       // 有界实例
Supplier<ArrayList<String>> ctor = ArrayList::new;     // 构造器
BiFunction<String, Integer, Person> personCtor = Person::new;
```

## 4. 函数式组合

`Function` 提供 `andThen`（先自己后参数）与 `compose`（先参数后自己）；`Predicate` 提供 `and` / `or` / `negate`。

```java
Function<Integer, Integer> doubleIt = x -> x * 2;
Function<Integer, Integer> plusOne = x -> x + 1;

doubleIt.andThen(plusOne).apply(5); // 11：先 *2 再 +1
doubleIt.compose(plusOne).apply(5); // 12：先 +1 再 *2

hasA.and(hasB).test("abc");
hasA.negate().test("xyz");
```

## 5. 变量捕获与 this 语义

Lambda 只能捕获 **effectively final** 的局部变量与参数（值不再改变即可，不必写 `final`）；字段不受限制，通过 `this` 访问。

```java
int base = 10;
// base = 20;           // 去掉注释则编译失败
IntUnaryOperator op = x -> x + base;
```

**`this` 语义**：lambda 没有自己的 `this`，`this` 就是外层实例；匿名内部类有自己的 `this`，会遮蔽外部的。

捕获的是**引用**：引用本身不能重新赋值，但对象内容可变。

## 6. 内置函数式接口（java.util.function）

| 接口 | 签名 | 用途 |
|------|------|------|
| `Function<T,R>` | `T -> R` | 转换 |
| `BiFunction<T,U,R>` | `(T,U) -> R` | 双参转换 |
| `Predicate<T>` | `T -> boolean` | 判断 |
| `Supplier<T>` | `() -> T` | 惰性提供值 |
| `Consumer<T>` | `T -> void` | 消费值 |
| `BiConsumer<T,U>` | `(T,U) -> void` | 消费两个值 |
| `UnaryOperator<T>` | `T -> T` | 一元运算 |
| `BinaryOperator<T>` | `(T,T) -> T` | 二元运算 |
| `IntFunction<R>` / `IntUnaryOperator` | `int -> R` / `int -> int` | 原始类型特化，避免装箱 |

典型应用：

```java
names.sort(Comparator.comparing(String::length));
names.removeIf(s -> s.startsWith("B"));
map.computeIfAbsent("fruit", k -> new ArrayList<>()).add("apple");
```

## 7. 运行本模块

**VS Code**：打开 `src/com/learn/lambda/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `LambdaSyntax.java` | 语法形式与目标类型 |
| `FunctionalInterfaces.java` | `@FunctionalInterface` 与重载歧义 |
| `MethodReferences.java` | 方法引用四种形式 |
| `FunctionalComposition.java` | andThen / compose / and / or / negate |
| `LambdaCapture.java` | effectively final 与 this 语义 |
| `BuiltInFunctionals.java` | java.util.function 与集合典型用法 |

## 易错点

1. 块体 lambda 多行时忘记写 `return`（表达式体不用写，块体必须写）
2. 修改被捕获的局部变量导致编译失败——它必须是 effectively final
3. 以为 lambda 里 `this` 指向 lambda 自身；lambda 的 `this` 就是外层实例
4. 重载方法参数都是函数式接口时，裸 lambda 报“目标类型不明确”，需显式转型
5. `andThen` 与 `compose` 的执行顺序搞反：`A.andThen(B)` 是先 A 后 B，`A.compose(B)` 是先 B 后 A
6. `UnaryOperator.andThen(...)` 的返回类型是 `Function` 而不是 `UnaryOperator`（`andThen` 声明在 `Function` 上），赋值时类型要写对
7. 大数据量场景用装箱的 `Function<Integer,Integer>` 而不是 `IntUnaryOperator`，白白付出装箱开销
8. 用匿名内部类硬写 SAM，其实可以用 lambda 或方法引用简化
