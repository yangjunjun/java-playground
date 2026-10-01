# 泛型（Generics）

泛型是 Java 5 引入的参数化类型机制：把“类型”当作参数，编译期做类型检查、免去强转，同时让集合/工具类一份代码服务多种类型。本模块覆盖泛型类/接口、泛型方法、有界参数、通配符与 PECS、类型擦除及其后果。

## 1. 泛型类与泛型接口

类型参数写在类名后，约定单字母：`T`（Type）、`K/V`（Key/Value）、`E`（Element）、`N`（Number）。

```java
class Box<T> {
    private T content;
    void put(T item) { this.content = item; }
    T get() { return content; }
}

interface Repository<T> {
    void save(T entity);
    T findById(int id);
}
```

使用时指定实参：`Box<String> box = new Box<>();`（右侧 `<>` 是钻石语法，Java 7+ 可省略实参）。

## 2. 泛型方法

`<T>` 写在返回值之前，类型参数只在本方法内有效，常可由编译器**类型推断**：

```java
static <T> T firstOf(List<T> list) {
    return list.isEmpty() ? null : list.get(0);
}
String s = firstOf(List.of("a", "b"));   // T 推断为 String
```

**varargs + 泛型**会产生“不可具体化类型”告警，若确认不往可变参数数组里塞不安全元素，可标注 `@SafeVarargs`（Java 7+，只能标在 static/final/构造器上）：

```java
@SafeVarargs
static <T> List<T> asList(T... items) {
    return List.of(items);
}
```

`List.of(...)`（Java 9+）：不可变列表、拒绝 null，适合作为返回值/常量。

## 3. 有界类型参数

`<T extends Bound>` 限制类型参数上界（类或接口都用 `extends`），从而安全调用上界的方法：

```java
static <T extends Comparable<T>> T max(List<T> list) {
    T best = list.get(0);
    for (T item : list) {
        if (item.compareTo(best) > 0) best = item;
    }
    return best;
}
```

多重上界：`<T extends Number & Comparable<T>>`——**类在前、接口在后**，且类只能有一个。

## 4. 通配符与 PECS

| 写法 | 含义 | 能读 | 能写 |
|------|------|------|------|
| `List<? extends Number>` | 上界通配符（协变） | 读为 `Number` | 不能 add 具体元素 |
| `List<? super Integer>` | 下界通配符（逆变） | 只能读为 `Object` | 可 add `Integer` 及子类 |
| `List<?>` | 无界通配符 | 读为 `Object` | 不能 add（null 除外） |

```java
static double sumOf(List<? extends Number> list) { ... } // 生产者：只读
static void fill(List<? super Integer> list) { list.add(1); } // 消费者：可写
```

**PECS 原则**：Producer Extends, Consumer Super——从集合取数据用 `? extends`，往集合放数据用 `? super`。

注意：`List<String>` **不是** `List<Object>` 的子类（泛型不变）；需要“父类型容器”时用通配符。

## 5. 类型擦除

泛型信息**只存在于编译期**，编译后擦除为上界（默认 `Object`）：

- `List<String>` 与 `List<Integer>` 的运行时 `getClass()` 相同；
- 不能 `new T[]`、不能 `new T()`；
- 不能 `instanceof List<String>`（只能 `instanceof List` 或 `List<?>`）；
- 重写泛型方法时，编译器生成**桥接方法（bridge method）**保持多态。

```java
// 编译器实际生成：
// void store(Object obj) { store((String) obj); }   // bridge
```

## 6. 泛型陷阱与最佳实践

1. **不要使用原始类型**（`List list`）：绕过检查，运行时 `ClassCastException`。
2. 基本类型不能做实参：用 `List<Integer>` 而不是 `List<int>`（自动装箱有开销）。
3. 不能创建泛型数组 `new Box<String>[10]`——改用 `List<Box<String>>`。
4. 不能实例化 `T`——需要工厂时传 `Supplier<T>`。
5. 方法返回集合时**返回空集合（`List.of()`）而不是 null**。
6. 通配符边界拿不准时回忆 PECS。

## 7. 运行本模块

**VS Code**：打开 `src/com/learn/generics/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `GenericClass.java` | 泛型类/接口 + Repository 实用示例 |
| `GenericMethod.java` | 泛型方法、类型推断、@SafeVarargs、List.of |
| `BoundedTypes.java` | 有界类型参数与多重上界 |
| `Wildcards.java` | ? extends / ? super / PECS |
| `TypeErasure.java` | 类型擦除后果与桥接方法 |
| `GenericPitfalls.java` | 原始类型、泛型数组等陷阱 |

## 易错点

1. 把 `List<String>` 赋给 `List<Object>` → 编译错误（泛型不变，用 `?`）
2. 用原始类型 `List` 混装不同类型 → 编译通过但运行时强转炸掉
3. 在 `List<? extends Number>` 里 `add(1)` → 编译错误（上界通配符只读）
4. 重写泛型父类方法时写错形参类型 → 没真正重写，变成重载（@Override 可帮助发现）
5. `@SafeVarargs` 标在普通实例方法（非 final）上 → 编译错误
6. 期望 `List<String>.class` 与 `List<Integer>.class` 不同 → 实际相同（类型擦除）
