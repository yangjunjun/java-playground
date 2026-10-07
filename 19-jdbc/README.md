# JDBC

JDBC（Java Database Connectivity）是 Java 访问关系型数据库的标准 API：`java.sql` / `javax.sql`。它定义了一套与具体数据库无关的接口，实际实现由各数据库驱动（Driver）提供。本模块因不引入外部 jar，使用内置的 `DemoDb` 内存假库模拟同一套调用形态——**代码结构与真实 JDBC 一致，但不是 `java.sql` 接口的实现**。

## 1. JDBC 架构

| 角色 | 职责 |
|------|------|
| `DriverManager` | 按 URL 选择驱动，建立 `Connection` |
| `Connection` | 一条数据库连接，控制事务 |
| `Statement` | 执行静态 SQL |
| `PreparedStatement` | 预编译 SQL，支持 `?` 参数绑定 |
| `ResultSet` | 查询结果的游标视图 |
| `DataSource` | 工厂式获取连接，连接池的基础 |

真实项目需要引入驱动 jar，并使用真实 JDBC URL：

```java
// MySQL 驱动：mysql-connector-j；旧代码需要 Class.forName("com.mysql.cj.jdbc.Driver")
Connection conn = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/demo?useSSL=false&serverTimezone=UTC",
        "root", "secret");

// PostgreSQL
Connection conn = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/demo", "postgres", "secret");

// H2（嵌入式，适合本地实验）
Connection conn = DriverManager.getConnection(
        "jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1", "sa", "");
```

JDBC 4.0+ 会通过 `META-INF/services/java.sql.Driver` 自动加载驱动，通常无需再写 `Class.forName`。

## 2. Statement 与 PreparedStatement

```java
try (PreparedStatement ps = conn.prepareStatement(
        "SELECT * FROM users WHERE id = ?")) {
    ps.setInt(1, 2);                    // 参数绑定
    try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            System.out.println(rs.getString("name"));
        }
    }
}
```

**PreparedStatement 的价值不只是性能（预编译），更是安全**——参数不会被当作 SQL 解析：

```java
// 危险：字符串拼接，存在 SQL 注入
String sql = "SELECT * FROM users WHERE name = '" + userInput + "'";
// 攻击者输入 ' OR '1'='1  →  WHERE name = '' OR '1'='1'  →  全表泄露
```

## 3. ResultSet 遍历

`ResultSet` 是游标模型：`next()` 前进一行，返回 `false` 表示结束；`next()` 之前不能取值。

```java
while (rs.next()) {
    int id = rs.getInt("id");
    String name = rs.getString("name");   // 按列名
    String email = rs.getString(3);       // 按列号（从 1 开始）
}
```

`rs.getMetaData()` 可拿到列数、列名、列类型，适合做通用导出。

## 4. 事务

```java
conn.setAutoCommit(false);   // 关闭自动提交，开启事务
try {
    // ... 多条更新 ...
    conn.commit();           // 全部成功才提交
} catch (Exception e) {
    conn.rollback();         // 任一失败整体回滚
}
```

保存点（Savepoint）可只回滚到中间某一步：

```java
Savepoint sp = conn.setSavepoint("before-delete");
// ...
conn.rollback(sp);
```

## 5. 批处理

一次网络往返提交多条 SQL，显著快于逐条 `executeUpdate`：

```java
try (PreparedStatement ps = conn.prepareStatement(
        "INSERT INTO users (id, name, email) VALUES (?, ?, ?)")) {
    for (User u : users) {
        ps.setInt(1, u.id());
        ps.setString(2, u.name());
        ps.setString(3, u.email());
        ps.addBatch();
    }
    int[] counts = ps.executeBatch();
}
```

## 6. try-with-resources 与资源关闭

`Connection`、`Statement`、`ResultSet` 都实现了 `AutoCloseable`，务必用 try-with-resources：

```java
try (Connection conn = ds.getConnection();
     PreparedStatement ps = conn.prepareStatement(sql);
     ResultSet rs = ps.executeQuery()) {
    // ...
} // 自动按逆序关闭：ResultSet → Statement → Connection
```

`ResultSet` 由 `Statement` 产生，关闭 `Statement` 会连带关闭其 `ResultSet`；显式关闭是为了及时释放数据库游标，避免连接池资源泄漏。

## 7. RowSet 与连接池

- **RowSet**：`CachedRowSet` 等支持“断开式”结果集，离线编辑后再同步回库。
- **连接池**：生产环境用 HikariCP、Druid、DBCP 管理 `Connection` 复用；不要每次 `DriverManager.getConnection`。

```java
HikariConfig cfg = new HikariConfig();
cfg.setJdbcUrl("jdbc:mysql://localhost:3306/demo");
cfg.setMaximumPoolSize(10);
DataSource ds = new HikariDataSource(cfg);
```

## 8. 运行本模块

**VS Code**：打开 `src/com/learn/jdbc/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

本模块不依赖真实数据库：`DemoDb.java` 是内置内存假库，只识别教学用到的几种 SQL 形态。

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `DemoDb.java` | 内存假库：模拟 Connection / Statement / PreparedStatement / ResultSet |
| `JdbcBasics.java` | 架构分层、Statement 查询、try-with-resources |
| `PreparedStatementDemo.java` | `?` 参数绑定 与 SQL 注入对比 |
| `TransactionDemo.java` | setAutoCommit / commit / rollback / 保存点 |
| `ResultSetDemo.java` | 游标遍历、列元数据 |
| `MetadataAndBatch.java` | DatabaseMetaData、批处理 |
| `JdbcPatterns.java` | DAO 分层、RowSet、连接池概念 |

## 易错点

1. 用 `Statement` + 字符串拼接处理用户输入 → SQL 注入；必须用 `PreparedStatement` 的 `?`
2. 忘记关闭 `ResultSet` / `Statement` / `Connection` → 连接池资源泄漏，最终 `CannotGetConnection`
3. `ResultSet.next()` 之前就 `getString` → 抛“游标不在有效行”类异常
4. 事务里忘了 `commit()` 就关连接（或自动提交没关）→ 变更悄悄丢失或自动落库
5. `setAutoCommit(false)` 后异常路径不 `rollback()` → 挂起事务占住连接
6. `getXxx` 列号从 **1** 开始，不是 0；列名大小写不敏感但别写错别名（`AS` 之后要用别名取）
