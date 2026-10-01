# 面向对象（OOP）

面向对象是 Java 的核心编程范式：把数据（字段）与行为（方法）封装进“类”，通过对象协作完成业务。四大特性是封装、继承、多态、抽象。本模块覆盖类与构造器、访问权限、继承与重写、动态分派、抽象类/接口、Object 方法与内部类，并简述密封类（sealed）等新语法。

## 1. 类与对象

类是模板，对象是实例。`new` 调用构造器创建对象：

```java
class Point {
    int x, y;
    Point(int x, int y) {
        this.x = x; // this 指向当前对象
        this.y = y;
    }
}
Point p = new Point(1, 2);
```

## 2. 构造器与构造器链

- 构造器与类同名、无返回值，可重载。
- `this(...)` 委托本类其他构造器；`super(...)` 调用父类构造器。
- 隐式 `super()` 要求父类有无参构造器；**子类构造器中 `super()/this(...)` 必须是第一条语句**。
- 构造顺序：先父类字段/构造器，后子类（“先父后子”）。

```java
class Account {
    Account(String owner) {
        this(owner, 0); // 构造器链，避免重复初始化
    }
    Account(String owner, double balance) { /* ... */ }
}
```

## 3. 访问权限

| 修饰符 | 同类 | 同包 | 子类 | 其他 |
|--------|:----:|:----:|:----:|:----:|
| `private` | √ | × | × | × |
| 默认（不写） | √ | √ | × | × |
| `protected` | √ | √ | √ | × |
| `public` | √ | √ | √ | √ |

字段通常 `private`，对外只暴露必要的方法——这就是封装。

## 4. 封装与 getter/setter

```java
class BankAccount {
    private double balance;
    public double getBalance() { return balance; }
    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("金额必须为正");
        balance += amount;
    }
}
```

封装的价值：隐藏实现、集中校验、日后可自由改内部表示而不影响调用方。

## 5. 继承

`extends` 表示 is-a 关系；子类可重写（`@Override`）父类方法，用 `super.` 调用父类实现。

```java
class Dog extends Animal {
    @Override
    void speak() {
        super.speak();       // 调用父类版本
        System.out.println("汪汪");
    }
}
```

- `final` 方法不能被重写；`final` 类不能被继承（如 `String`）。
- 单继承：一个类只能 `extends` 一个父类，但可 `implements` 多个接口。

## 6. 多态

父类引用指向子类对象（向上转型），运行时按真实类型分派方法（动态分派）：

```java
Shape s = new Circle(2);
s.area(); // 调用 Circle.area()

// instanceof 模式匹配（Java 16+）：判断并强转一步到位
if (s instanceof Circle c) {
    System.out.println(c.r);
}
```

`switch` 模式匹配（Java 21+）可进一步简化类型分支：

```java
String desc = switch (s) {
    case Circle c -> "圆 r=" + c.r;
    case Rect r   -> "矩形 " + r.w + "x" + r.h;
    default       -> "其他";
};
```

## 7. 抽象类与接口

| 对比 | 抽象类 `abstract class` | 接口 `interface` |
|------|------------------------|------------------|
| 字段 | 可有实例字段 | 只能是 `public static final` 常量 |
| 构造器 | 有 | 无 |
| 继承 | 单继承 | 可实现多个 |
| 方法 | 可具体可抽象 | 抽象 + `default`（Java 8）+ `static`（Java 8）+ `private`（Java 9） |

```java
interface Greeter {
    String greet(String name);              // 抽象
    default String polite(String name) {    // Java 8：带默认实现
        return "【" + greet(name) + "】";
    }
    static String shout(String s) { return s + "!"; } // Java 8
    private String wrap(String s) { return "[" + s + "]"; } // Java 9：仅供接口内部用
}
```

多个接口有同名 `default` 方法时，实现类必须重写，可用 `InterfaceName.super.method()` 选择其一。

**密封类/接口（sealed，Java 17+）** 可精确限定子类集合，适合表达完整类型层级：

```java
sealed interface Shape permits Circle, Rect {}
final class Circle implements Shape {}
final class Rect implements Shape {}
```

## 8. Object 常用方法

| 方法 | 说明 |
|------|------|
| `toString()` | 字符串表示；不重写时是 `类名@哈希` |
| `equals(Object)` | 逻辑相等；**重写它必须同时重写 `hashCode()`** |
| `hashCode()` | 哈希值；`equals` 为 true 的两对象哈希必须相同 |
| `clone()` | 浅拷贝；需 `implements Cloneable` 并把 `clone()` 改为 `public` |
| `finalize()` | **已过时（Java 18+ `@Deprecated(forRemoval)`），禁止用于资源释放**；改用 try-with-resources 或 `Cleaner`（Java 9+） |

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Point p)) return false;
    return x == p.x && y == p.y;
}
@Override
public int hashCode() { return 31 * x + y; }
```

## 9. 内部类

| 类型 | 特点 |
|------|------|
| 成员内部类 | 依附外部实例，可访问外部私有成员；`outer.new Inner()` 创建 |
| 静态嵌套类 | 无外部实例引用，相当于“只是碰巧放在别的类里” |
| 局部内部类 | 定义在方法内，作用域限于该方法 |
| 匿名内部类 | 一次性实现接口/抽象类，无类名（Lambda 的前身） |

```java
class Outer {
    private String msg = "hi";
    class Inner { void show() { System.out.println(msg); } }   // 成员内部类
    static class Nested { }                                    // 静态嵌套
    void m() {
        class Local { }                                        // 局部内部类
        Runnable r = new Runnable() {                          // 匿名内部类
            public void run() { System.out.println("run"); }
        };
    }
}
```

## 10. 运行本模块

**VS Code**：打开 `src/com/learn/oop/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `Encapsulation.java` | 类、构造器链、访问权限、getter/setter |
| `Inheritance.java` | 继承、重写、super、final |
| `Polymorphism.java` | 向上转型、动态分派、instanceof/switch 模式匹配 |
| `AbstractInterface.java` | 抽象类与接口（default/static/private 方法） |
| `InnerClasses.java` | 成员/静态嵌套/局部/匿名内部类 |
| `ObjectMethods.java` | toString / equals / hashCode / clone |

## 易错点

1. `super(...)`/`this(...)` 不是构造器第一条语句 → 编译错误
2. 重写 `equals` 却不重写 `hashCode` → 作为 HashMap 键时行为异常
3. `clone()` 忘记 `implements Cloneable` → 运行时 `CloneNotSupportedException`
4. 子类重写方法时缩小访问权限（如 public → private）→ 编译错误
5. 用 `finalize()` 释放资源 → 不可靠且已过时，应使用 try-with-resources
6. 成员内部类误用 `new Inner()` 而不是 `outer.new Inner()` → 编译错误
