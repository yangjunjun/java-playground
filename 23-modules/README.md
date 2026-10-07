# Java 模块系统 JPMS（Java Platform Module System）

JPMS 是 Java 9（JEP 261）引入的模块化机制，也叫 Project Jigsaw。它把「包」聚合成「模块」，在编译期与运行期强制声明依赖与可见性，解决 classpath 上封装失效、依赖不可见、镜像臃肿等问题。本模块包含可运行的单模块示例，以及 `multi/` 下**真正双模块编译运行**示例。

## 1. 为什么需要 JPMS

| 问题（classpath） | JPMS 的答案 |
|---|---|
| 任何 public 类都能被反射乱调 | 只有 `exports` 的包才对外可见 |
| 依赖关系写在 POM/Gradle 里，运行期不校验 | `requires` 在编译期与启动期都校验 |
| 同一 jar 多版本共存，行为不确定 | 模块图在启动时一次性解析，冲突直接报错 |
| 运行时镜像臃肿（整个 JDK） | `jlink` 只打包用到的模块 |

三大目标：**强封装**（encapsulation）、**可靠配置**（reliable configuration）、**可扩展平台**（scalable platform）。

## 2. 模块声明 module-info.java

每个模块根目录放一个 `module-info.java`：

```java
// greet 模块：导出 com.learn.greet 包
module greet {
    exports com.learn.greet;
}

// app 模块：依赖 greet
module app {
    requires greet;
    uses com.learn.modules.GreetingService;      // 声明我要用服务
}
```

## 3. 关键指令

| 指令 | 含义 |
|------|------|
| `requires` | 依赖另一个模块（`requires transitive` 传递依赖，`requires static` 可选依赖） |
| `exports` | 把包导出给其他模块（编译期可见） |
| `opens` | 把包开放给反射（运行期可 `setAccessible`） |
| `exports ... to` | 只导出给指定模块（限定导出） |
| `provides ... with ...` | 声明服务实现（配合 `uses`） |
| `uses` | 声明我要通过 `ServiceLoader` 消费某服务 |

```java
module app {
    requires transitive greet;                      // 传递导出
    exports com.learn.app.api;                      // 导出
    opens com.learn.app.model to spring.core;       // 只给 spring.core 反射
    provides GreetingService with EnglishGreeting;  // 提供服务实现
}
```

## 4. --module-path vs -classpath

```bash
# 旧：类路径，一切进 unnamed module，封装全开
javac -classpath lib/* -d out src/Main.java
java  -classpath out:lib/* Main

# 新：模块路径，每个 jar/目录是命名模块
javac --module-path lib -d out $(find src -name '*.java')
java  --module-path out -m app/main.Main
```

| | `-classpath` | `--module-path` |
|---|---|---|
| 模块归属 | 全部 unnamed | 命名模块 |
| 封装 | 失效 | 生效 |
| 依赖校验 | 无 | 编译期 + 启动期 |
| 反射 | 几乎不受限 | 需 `opens` |

**自动模块**（automatic module）：普通 jar 放上 `--module-path` 会成为自动模块，
模块名从 jar 文件名推导（或 `Automatic-Module-Name`），自动导出所有包（封装失效）。
用途：迁移期让老 jar 被模块使用，是过渡手段而非终点。

## 5. ServiceLoader 服务发现（Java 6+ / 9+ 模块化）

接口 + 多个实现，运行时动态发现，解耦「服务定义」与「服务提供」：

```java
// 服务接口
public interface GreetingService {
    String greet(String name);
}

// 消费方（module-info 中 uses GreetingService）
ServiceLoader<GreetingService> loader = ServiceLoader.load(GreetingService.class);
for (GreetingService s : loader) {
    System.out.println(s.greet("模块"));
}
```

**classpath 配置**（老写法）：`META-INF/services/com.learn.GreetingService` 文件里每行一个实现类全名。

**模块配置**（Java 9+）：

```java
// 提供者模块
module greeter.impl {
    requires greeter.api;
    provides GreetingService with EnglishGreeting, ChineseGreeting;
}

// 消费方模块
module app {
    requires greeter.api;
    uses GreetingService;
}
```

## 6. 运行时自省：Module / ModuleDescriptor

```java
Module m = String.class.getModule();
m.isNamed();                                  // 是否命名模块
m.getDescriptor().exports();                  // 导出包列表
ModuleLayer.boot().modules();                 // 启动层所有模块
m.isExported("java.lang", caller);            // 是否导出给某模块
m.getDescriptor().isOpen();                   // 是否 open 模块
```

classpath 运行时当前模块是 **unnamed module**：对所有包自动导出，反射几乎不受限——这就是为什么 classpath 上「封装失效」。

## 7. 多模块编译示例（modules/multi/）

真实的双模块工程，`greet` 模块被 `app` 模块 `requires`：

```
modules/multi/
├── greet/
│   ├── module-info.java          # module greet { exports com.learn.greet; }
│   └── com/learn/greet/Greeter.java
├── app/
│   ├── module-info.java          # module app { requires greet; }
│   └── main/Main.java
└── run.sh                        # 编译 + 运行 + --describe-module
```

核心命令：

```bash
# 1. 编译 greet（先编译被依赖方）
javac -d out/greet $(find greet -name '*.java')

# 2. 编译 app（--module-path 才能 requires greet）
javac --module-path out -d out/app $(find app -name '*.java')

# 3. 运行：-m 模块名/主类全名
java --module-path out -m app/main.Main

# 4. 查看模块描述符
java --module-path out --describe-module app
```

## 8. jlink 裁剪运行时（概念）

```bash
# 只打包用到的模块，生成精简运行时镜像（无需整套 JDK）
jlink --module-path $JAVA_HOME/jmods:out \
      --add-modules app \
      --output my-image

./my-image/bin/java -m app/main.Main
```

镜像可小到几十 MB，适合容器与桌面分发。详见 `JLinkNotes` 说明（本模块未演示，因为需要 jmods 完整路径）。

## 9. 迁移建议与反射 --add-opens

**迁移路线**：

1. **先跑通 classpath**——别急着上模块
2. 给 jar 加 `Automatic-Module-Name`，放到 `--module-path` 上成为自动模块
3. 逐步补 `module-info.java`，从叶子模块（无依赖）开始
4. 框架（Spring/Jackson）需要反射的包用 `opens` 开放

**反射被拒的典型报错**：

```
java.lang.reflect.InaccessibleObjectException:
  Unable to make field private final ... accessible:
  module java.base does not "opens java.lang" to module app
```

解决（运行时参数，应急用）：

```bash
java --add-opens java.base/java.lang=ALL-UNNAMED ...
java --add-opens java.base/java.util=app ...
```

**建议**：`--add-opens` 是过渡手段，长期应改用 `opens` 声明或公开 API。

## 10. 更前沿（Java 23 ~ 25，仅作了解）

| 特性 | 版本 | 说明 |
|------|------|------|
| 模块导入声明 `import module java.base;` | Java 25 | 一次导入整个模块的导出包 |
| 紧凑源文件与实例 main | Java 25 | 单文件 `void main()`，无需 class |
| 原始类型模式匹配 | Java 25 **预览** | 需 `--enable-preview` |

## 运行本模块

**VS Code**：打开 `src/com/learn/modules/Main.java`，点击 `main` 上方的 **Run**。

**命令行**（含多模块示例）：

```bash
./run.sh
```

只想跑多模块示例：

```bash
cd multi && ./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `ModuleBasics.java` | Module / ModuleDescriptor / ModuleLayer 自省 |
| `PackageEncapsulationDemo.java` | 公开类型 vs 包导出、反射差异 |
| `ServiceLoaderDemo.java` | 服务接口 + 两个提供者 + ServiceLoader |
| `META-INF/services/...` | classpath 服务提供者声明（run.sh 会拷入 out/） |
| `ModulePathVsClasspath.java` | 两种路径对比、自动模块、启动层模块清单 |
| `multi/greet/` | 被依赖的 greet 模块（导出 com.learn.greet） |
| `multi/app/` | 依赖 greet 的 app 模块（`-m app/main.Main`） |
| `multi/run.sh` | 双模块编译运行脚本 |

## 易错点

1. `exports` 管**编译期可见性**，`opens` 管**反射访问**——只 exports 不 opens，反射 `setAccessible` 会被拒
2. `requires` 缺失 → 编译期就报 `package ... is not visible`，别等运行期才发现
3. 自动模块自动导出所有包，封装等于没开——迁移过渡用，别长期依赖
4. classpath 上一切是 unnamed module，`Module.isExported` 对 unnamed 恒真，别误判成「已导出」
5. `module-info.java` 必须在**模块根目录**（与包平级），放错位置编译器找不到
6. `provides ... with ...` 的实现类必须有无参构造（或 Provider 工厂）
7. `--add-opens` 能解一时之急，但掩盖了封装设计问题，别滥用
8. `jlink` 只能打包**模块化** jar；自动模块与 classpath jar 不可用 jlink
9. 编译顺序：先编译被依赖模块（greet），再编译依赖方（app），且必须给 `--module-path`
