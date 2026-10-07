# 数组（Array）

数组是 Java 里唯一内建的容器：定长、可存基本类型或引用类型、通过下标随机访问。本模块覆盖声明与初始化、复制、多维数组、`java.util.Arrays` 工具类，以及排序与二分查找。`ArrayList` 等集合容器见 collections 模块。

## 1. 数组基础

声明与初始化：

```java
int[] a = new int[5];          // 先声明后使用，默认全 0
int[] b = { 1, 2, 3 };         // 静态初始化
int[] c = new int[] { 1, 2 };  // 匿名数组
String[] names = new String[3]; // 引用数组默认全 null
```

`int[] a` 与 `int a[]` 等价，推荐前者。数组创建后**长度不可变**，`length` 是字段（不是方法）：

```java
System.out.println(b.length);        // 3
System.out.println(b[0]);            // 下标从 0 开始
// b[3] 越界 → ArrayIndexOutOfBoundsException
```

元素默认值与字段一致：数值 `0`、浮点 `0.0`、`char` `'\u0000'`、`boolean` `false`、引用 `null`。遍历用 for-each 或经典 for（需要下标时用后者）。

### 数组协变与 ArrayStoreException

数组是**协变**的：`String[]` 可以赋给 `Object[]`。但运行时仍按真实元素类型检查：

```java
Object[] arr = new String[2];
arr[0] = "ok";
arr[1] = 100;   // 编译通过，运行时 ArrayStoreException
```

## 2. 数组复制

三种方式，注意引用数组都是**浅拷贝**（只复制引用，不复制对象）：

```java
int[] src = { 1, 2, 3, 4, 5 };
int[] c1 = src.clone();                             // 整份复制
int[] c2 = Arrays.copyOf(src, 3);                   // {1,2,3}，可扩容/截断
int[] c3 = new int[3];
System.arraycopy(src, 1, c3, 0, 3);                 // 从 src[1] 复制 3 个到 c3[0]
```

| 方式 | 特点 |
|------|------|
| `clone()` | 数组自带方法，复制整个数组 |
| `System.arraycopy` | 本地方法最快，可指定区间与目标位置 |
| `Arrays.copyOf` / `copyOfRange` | 底层就是 arraycopy，可顺便改长度 |

## 3. 多维数组

二维数组是“数组的数组”，**行与行长度可以不同**（不规则数组）：

```java
int[][] m = new int[2][3];      // 2 行 3 列，全 0
int[][] jagged = {
        { 1 },
        { 1, 2 },
        { 1, 2, 3 }
};
System.out.println(jagged.length);      // 3 行
System.out.println(jagged[1].length);   // 第 2 行有 2 个元素
```

`m[i][j]` 访问第 i 行第 j 列；`new int[2][]` 先建行数组，再逐行建列。三维及以上同理，但业务里很少用，通常改用对象或 Map。

## 4. java.util.Arrays 工具类

| 方法 | 作用 |
|------|------|
| `toString(a)` | 一维数组可读输出 `[1, 2, 3]` |
| `deepToString(a)` | 多维数组递归输出 |
| `fill(a, v)` / `fill(a, from, to, v)` | 全部/区间填充 |
| `copyOf` / `copyOfRange` | 复制 / 截取区间 |
| `equals(a, b)` / `deepEquals` | 逐元素相等判断（不是比引用） |
| `setAll(a, i -> expr)` | 按下标计算填充（Java 8+） |
| `compare(a, b)` | 字典序比较（Java 9+），返回负/0/正 |
| `mismatch(a, b)` | 第一个不同元素的下标（Java 9+） |

```java
int[] arr = new int[3];
Arrays.setAll(arr, i -> i * 10);        // [0, 10, 20]
System.out.println(Arrays.toString(arr));
```

`List.of(...)` 会生成**不可变 List**（Java 9+），与数组的取舍：

```java
List<String> list = List.of("a", "b");  // 不可变、越界即抛异常
String[] arr = { "a", "b" };            // 定长但元素可改，可存基本类型
```

## 5. 排序与查找

```java
int[] a = { 3, 1, 2 };
Arrays.sort(a);                 // 基本类型：双轴快排，原地排序
Arrays.parallelSort(a);         // 大数组并行排序（Java 8+）
int idx = Arrays.binarySearch(a, 2); // 必须先排好序！
```

要点：

- `sort` 对基本类型数组用双轴快速排序，对对象数组用 TimSort（对象需实现 `Comparable` 或传 `Comparator`）
- `binarySearch` 前提是已按升序排序，否则结果未定义；找不到返回 `-(插入点) - 1`
- 对象数组排序只移动引用，对象本身不复制
- `Integer[]` 与 `int[]` 不能互换：排序、`Arrays.equals` 的行为按元素类型走

## 6. 运行本模块

**VS Code**：打开 `src/com/learn/array/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `ArrayBasics.java` | 声明、默认值、length、协变陷阱 |
| `ArrayCopy.java` | clone / arraycopy / copyOf |
| `MultiDimArray.java` | 二维与不规则数组 |
| `ArraysUtil.java` | Arrays 常用工具与 List.of 对比 |
| `ArraySortSearch.java` | sort / parallelSort / binarySearch |

## 易错点

1. 数组长度是 `length` 字段，字符串才是 `length()` 方法
2. 数组创建后长度固定，扩容只能建新数组再复制
3. `Object[] arr = new String[1]; arr[0] = 1;` 编译通过但运行时抛 `ArrayStoreException`
4. `binarySearch` 用在未排序数组上结果无意义
5. `Arrays.equals` 比内容，`==` 比引用，别混用
6. `int[]` 与 `Integer[]` 不能互相赋值；`List<int[]>` 里存的是数组对象
7. `clone()` 对引用数组是浅拷贝，复制后两份数组共享元素对象
