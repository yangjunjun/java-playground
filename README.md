# Java 基础知识练习仓库

本仓库用于系统学习 Java 基础知识。每个子文件夹专注一个知识点，包含说明文档与可运行的练习代码。

## 目录约定

```
<topic>/
├── README.md     # 该知识点的讲解与要点
├── run.sh        # 命令行一键编译运行（可选）
└── src/          # 练习源码
```

| 文件夹 | 知识点 |
|--------|--------|
| [annotation/](annotation/) | 注解（Annotation） |

## 环境要求

- JDK 8+（本仓库以 Java 8 编写）
- VS Code + Extension Pack for Java
- 可选：Maven 3.x、Bash（用于 `run.sh`）

## 如何一键运行

### 方式一：VS Code（推荐）

1. 用 VS Code 打开**本仓库根目录**
2. 打开 `annotation/src/.../Main.java`
3. 点击 `main` 方法上方的 **Run** / **Debug** 按钮

也可以按 **F5**，使用 `.vscode/launch.json` 中的启动配置。

### 方式二：命令行

```bash
cd annotation
./run.sh
```

或手动编译运行：

```bash
cd annotation
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name '*.java')
java -cp out com.learn.annotation.Main
```

## 如何新增知识点

1. 新建文件夹，名称使用英文小写（如 `exception`、`generics`、`io`）
2. 创建 `README.md` 写清概念、语法、易错点
3. 在 `src/` 下放练习代码，提供带 `public static void main` 的入口类
4. （可选）提供 `run.sh` 方便命令行运行
5. 在 `.vscode/settings.json` 的 `java.project.sourcePaths` 中追加 `"<topic>/src"`
6. 更新本文件的目录表格
