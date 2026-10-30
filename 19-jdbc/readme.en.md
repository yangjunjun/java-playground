# JDBC

JDBC (Java Database Connectivity) is the standard Java API for accessing relational databases: `java.sql` / `javax.sql`. It defines a set of database-agnostic interfaces, with concrete implementations supplied by each database's driver. Because this module does not pull in external jars, it uses the built-in `DemoDb` in-memory fake database to mimic the same call patterns — **the code structure matches real JDBC, but it is not an implementation of the `java.sql` interfaces**.

## 1. JDBC Architecture

| Role | Responsibility |
|------|------|
| `DriverManager` | selects a driver by URL and establishes a `Connection` |
| `Connection` | a database connection; controls transactions |
| `Statement` | executes static SQL |
| `PreparedStatement` | precompiled SQL with `?` parameter binding |
| `ResultSet` | a cursor view of query results |
| `DataSource` | factory-style connection acquisition; the basis for connection pools |

Real projects need the driver jar and a real JDBC URL:

```java
// MySQL driver: mysql-connector-j; older code needs Class.forName("com.mysql.cj.jdbc.Driver")
Connection conn = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/demo?useSSL=false&serverTimezone=UTC",
        "root", "secret");

// PostgreSQL
Connection conn = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/demo", "postgres", "secret");

// H2 (embedded, good for local experiments)
Connection conn = DriverManager.getConnection(
        "jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1", "sa", "");
```

JDBC 4.0+ loads drivers automatically via `META-INF/services/java.sql.Driver`, so `Class.forName` is usually no longer needed.

## 2. Statement and PreparedStatement

```java
try (PreparedStatement ps = conn.prepareStatement(
        "SELECT * FROM users WHERE id = ?")) {
    ps.setInt(1, 2);                    // parameter binding
    try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            System.out.println(rs.getString("name"));
        }
    }
}
```

**The value of PreparedStatement is not just performance (precompilation) but security** — bound parameters are never parsed as SQL:

```java
// Dangerous: string concatenation, SQL injection
String sql = "SELECT * FROM users WHERE name = '" + userInput + "'";
// attacker input ' OR '1'='1  →  WHERE name = '' OR '1'='1'  →  leaks the entire table
```

## 3. Traversing a ResultSet

`ResultSet` follows a cursor model: `next()` advances one row and returns `false` at the end; you cannot read values before calling `next()`.

```java
while (rs.next()) {
    int id = rs.getInt("id");
    String name = rs.getString("name");   // by column name
    String email = rs.getString(3);       // by column index (1-based)
}
```

`rs.getMetaData()` gives you the column count, names, and types — handy for generic exports.

## 4. Transactions

```java
conn.setAutoCommit(false);   // turn off auto-commit to start a transaction
try {
    // ... multiple updates ...
    conn.commit();           // commit only if everything succeeded
} catch (Exception e) {
    conn.rollback();         // roll everything back if any step fails
}
```

A savepoint lets you roll back to an intermediate step:

```java
Savepoint sp = conn.setSavepoint("before-delete");
// ...
conn.rollback(sp);
```

## 5. Batching

Submitting many SQL statements in one network round trip is dramatically faster than calling `executeUpdate` one by one:

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

## 6. try-with-resources and Resource Cleanup

`Connection`, `Statement`, and `ResultSet` all implement `AutoCloseable`; always use try-with-resources:

```java
try (Connection conn = ds.getConnection();
     PreparedStatement ps = conn.prepareStatement(sql);
     ResultSet rs = ps.executeQuery()) {
    // ...
} // closed automatically in reverse order: ResultSet → Statement → Connection
```

A `ResultSet` is produced by its `Statement`, and closing the `Statement` closes its `ResultSet` as well; explicit closing releases database cursors promptly and avoids connection pool resource leaks.

## 7. RowSet and Connection Pools

- **RowSet**: `CachedRowSet` and friends support disconnected result sets that you can edit offline and then sync back to the database.
- **Connection pool**: production code uses HikariCP, Druid, or DBCP to manage `Connection` reuse; do not call `DriverManager.getConnection` for every request.

```java
HikariConfig cfg = new HikariConfig();
cfg.setJdbcUrl("jdbc:mysql://localhost:3306/demo");
cfg.setMaximumPoolSize(10);
DataSource ds = new HikariDataSource(cfg);
```

## 8. Running This Module

**VS Code**: open `src/com/learn/jdbc/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

This module does not depend on a real database: `DemoDb.java` is a built-in in-memory fake that only understands the handful of SQL shapes used in the lessons.

## Practice Files

| File | Contents |
|------|------|
| `Main.java` | main entry point, runs all examples in order |
| `DemoDb.java` | in-memory fake database: mimics Connection / Statement / PreparedStatement / ResultSet |
| `JdbcBasics.java` | architecture layers, Statement queries, try-with-resources |
| `PreparedStatementDemo.java` | `?` parameter binding vs SQL injection comparison |
| `TransactionDemo.java` | setAutoCommit / commit / rollback / savepoints |
| `ResultSetDemo.java` | cursor traversal, column metadata |
| `MetadataAndBatch.java` | DatabaseMetaData, batching |
| `JdbcPatterns.java` | DAO layering, RowSet, connection pool concepts |

## Common Pitfalls

1. Handling user input with `Statement` + string concatenation leads to SQL injection; use `PreparedStatement` `?` binding instead
2. Forgetting to close `ResultSet` / `Statement` / `Connection` leaks connection pool resources and eventually causes `CannotGetConnection`
3. Calling `getString` before `ResultSet.next()` throws a "cursor is not on a valid row" style exception
4. Closing the connection without `commit()` in a transaction (or leaving auto-commit on) silently loses changes or writes them through immediately
5. After `setAutoCommit(false)`, not calling `rollback()` on the exception path leaves a pending transaction holding the connection
6. Column indexes for `getXxx` are **1**-based, not 0; column names are case-insensitive, but do not misuse aliases (after `AS`, fetch by the alias)
