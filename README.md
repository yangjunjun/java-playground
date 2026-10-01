# Java 基础知识练习仓库

本仓库用于系统学习 Java 基础知识（覆盖 Java 8 → Java 25 LTS）。每个子文件夹专注一个知识点，包含说明文档与可运行的练习代码。

## 目录约定

```
<topic>/
├── README.md     # 该知识点的讲解与要点
├── run.sh        # 命令行一键编译运行（可选）
└── src/          # 练习源码
```

## 知识点目录

| 文件夹 | 知识点 | 关键内容 |
|--------|--------|----------|
| [basics/](basics/) | 语言基础 | 变量、数据类型、运算符、控制流 |
| [string/](string/) | 字符串与文本 | String、StringBuilder、文本块、格式化 |
| [array/](array/) | 数组 | 声明、复制、多维数组、Arrays 工具类 |
| [wrapper/](wrapper/) | 包装类 | 自动装箱拆箱、Integer 缓存陷阱 |
| [oop/](oop/) | 面向对象 | 封装、继承、多态、抽象类、接口、内部类 |
| [exception/](exception/) | 异常处理 | 受检/非受检、try-with-resources、自定义异常 |
| [generics/](generics/) | 泛型 | 通配符、类型擦除、有界类型 |
| [enums/](enums/) | 枚举 | 常量方法、EnumSet/EnumMap、策略枚举 |
| [annotation/](annotation/) | 注解 | 内置/自定义/元注解、反射读取、重复注解 |
| [collections/](collections/) | 集合框架 | List/Set/Map/Queue、不变工厂、Sequenced Collections |
| [lambda/](lambda/) | Lambda 与函数式接口 | 方法引用、java.util.function、函数组合 |
| [stream/](stream/) | Stream API | 中间/终结操作、Collectors、并行流 |
| [optional/](optional/) | Optional | 创建、map/flatMap、orElse 族 |
| [datetime/](datetime/) | 日期时间 | LocalDate/LocalDateTime/Instant、时区、格式化 |
| [io/](io/) | IO 与 NIO | 字节/字符流、Path/Files、通道与缓冲区 |
| [concurrency/](concurrency/) | 并发编程 | 线程、锁、线程池、CompletableFuture |
| [reflection/](reflection/) | 反射 | Class、动态调用、动态代理 |
| [regex/](regex/) | 正则表达式 | Pattern/Matcher、分组、替换 |
| [jdbc/](jdbc/) | JDBC | 连接、PreparedStatement、事务、批处理 |
| [network/](network/) | 网络编程 | Socket/TCP、URL、HttpClient |
| [crypto/](crypto/) | 加密与安全 | 摘要、对称/非对称加密、数字签名 |
| [i18n/](i18n/) | 国际化 | Locale、ResourceBundle、消息与数字格式 |
| [modules/](modules/) | 模块系统 | JPMS、ServiceLoader、模块路径 |
| [modern-syntax/](modern-syntax/) | 现代语法 | var、switch 表达式、Record、密封类、模式匹配 |
| [virtual-threads/](virtual-threads/) | 虚拟线程 | 虚拟线程、结构化并发、ScopedValue |

## 环境要求

- **JDK 25 LTS**（本仓库以 Java 25 编写/验证，向下兼容 Java 8 语法；带 `（Java X+）` 标注的特性需要对应版本）
- 推荐用 [SDKMAN!](https://sdkman.io/) 安装：`sdk install java 25.0.2-tem && sdk default java 25.0.2-tem`
- VS Code + Extension Pack for Java
- 可选：Bash（用于 `run.sh`）

## 如何一键运行

### 方式一：VS Code（推荐）

1. 用 VS Code 打开**本仓库根目录**
2. 打开 `<topic>/src/.../Main.java`
3. 点击 `main` 方法上方的 **Run** / **Debug** 按钮

也可以按 **F5**，在启动配置列表中选择 `Run <Topic> Main`。

### 方式二：命令行

```bash
cd <topic>
./run.sh
```

或一次运行全部知识点：

```bash
./scripts/check-all.sh
```

## 如何新增知识点

1. 新建文件夹，名称使用英文小写（如 `exception`、`generics`、`io`）
2. 创建 `README.md` 写清概念、语法、易错点
3. 在 `src/com/learn/<topic>/` 下放练习代码，提供带 `public static void main` 的 `Main` 入口类
4. 提供 `run.sh` 方便命令行运行
5. 在 `.vscode/settings.json` 的 `java.project.sourcePaths` 中追加 `"<topic>/src"`
6. 在 `.vscode/launch.json` 中追加对应的启动配置
7. 更新本文件的目录表格
