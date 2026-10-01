package com.learn.jdbc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 教学用内存假数据库（DemoDb）。
 * <p>
 * 本仓库不引入真实 JDBC 驱动，也无外部数据库，因此这里用纯 JDK 实现一个
 * “看起来像 JDBC”的最小内存库：Connection / Statement / PreparedStatement /
 * ResultSet 都支持 try-with-resources，能演示遍历、参数绑定、批处理、事务等形态。
 * <p>
 * 它<b>不</b>实现 {@code java.sql.*} 接口，也<b>不</b>解析 SQL——只识别教学用到的
 * 几种固定 SQL 形态。真实项目请用 MySQL / PostgreSQL / H2 驱动，详见 README。
 */
public final class DemoDb {

    private DemoDb() {
    }

    /** 打开一个“连接”。每次调用都返回独立内存库副本（users 表预置 3 行）。 */
    public static DemoConnection connect() {
        List<Map<String, Object>> users = new ArrayList<>();
        users.add(row(1, "张三", "zhangsan@example.com"));
        users.add(row(2, "李四", "lisi@example.com"));
        users.add(row(3, "王五", "wangwu@example.com"));
        return new DemoConnection(users);
    }

    private static Map<String, Object> row(int id, String name, String email) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("id", id);
        r.put("name", name);
        r.put("email", email);
        return r;
    }

    // ------------------------------------------------------------------
    // Connection
    // ------------------------------------------------------------------

    /** 模拟 {@code java.sql.Connection}：支持自动提交开关、事务、保存点、批处理入口。 */
    public static final class DemoConnection implements AutoCloseable {

        private final List<Map<String, Object>> users;
        private boolean autoCommit = true;
        private List<Map<String, Object>> txSnapshot;
        private boolean closed;

        private DemoConnection(List<Map<String, Object>> users) {
            this.users = users;
        }

        public DemoStatement createStatement() {
            checkOpen();
            return new DemoStatement(this);
        }

        public DemoPreparedStatement prepareStatement(String sql) {
            checkOpen();
            return new DemoPreparedStatement(this, sql);
        }

        public void setAutoCommit(boolean autoCommit) {
            checkOpen();
            this.autoCommit = autoCommit;
            if (!autoCommit) {
                txSnapshot = deepCopy(users);
            }
        }

        public boolean getAutoCommit() {
            return autoCommit;
        }

        public void commit() {
            checkOpen();
            // 真实 JDBC：把事务内变更持久化；这里只需丢弃快照
            txSnapshot = null;
        }

        public void rollback() {
            checkOpen();
            if (txSnapshot != null) {
                users.clear();
                users.addAll(txSnapshot);
                txSnapshot = null;
            }
        }

        /** 保存点：把当前数据状态做成可回滚的检查点。 */
        public DemoSavepoint createSavepoint(String name) {
            checkOpen();
            return new DemoSavepoint(name, deepCopy(users));
        }

        public void rollback(DemoSavepoint sp) {
            checkOpen();
            users.clear();
            users.addAll(sp.snapshot);
        }

        public DemoDatabaseMetaData getMetaData() {
            checkOpen();
            return new DemoDatabaseMetaData();
        }

        List<Map<String, Object>> users() {
            return users;
        }

        @Override
        public void close() {
            closed = true;
        }

        private void checkOpen() {
            if (closed) {
                throw new IllegalStateException("连接已关闭");
            }
        }

        private static List<Map<String, Object>> deepCopy(List<Map<String, Object>> src) {
            List<Map<String, Object>> copy = new ArrayList<>();
            for (Map<String, Object> r : src) {
                copy.add(new LinkedHashMap<>(r));
            }
            return copy;
        }
    }

    /** 模拟 Savepoint：名字 + 当时的数据快照。 */
    public static final class DemoSavepoint {
        final String name;
        final List<Map<String, Object>> snapshot;

        DemoSavepoint(String name, List<Map<String, Object>> snapshot) {
            this.name = name;
            this.snapshot = snapshot;
        }

        public String getName() {
            return name;
        }
    }

    // ------------------------------------------------------------------
    // Statement / PreparedStatement
    // ------------------------------------------------------------------

    /** 模拟 {@code java.sql.Statement}：SQL 直接拼进方法参数，不支持 ? 占位符。 */
    public static final class DemoStatement implements AutoCloseable {

        private final DemoConnection conn;
        private boolean closed;

        DemoStatement(DemoConnection conn) {
            this.conn = conn;
        }

        public DemoResultSet executeQuery(String sql) {
            checkOpen();
            return SqlEngine.query(conn.users(), sql, new Object[0]);
        }

        public int executeUpdate(String sql) {
            checkOpen();
            return SqlEngine.update(conn.users(), sql, new Object[0], conn.autoCommit);
        }

        @Override
        public void close() {
            closed = true;
        }

        private void checkOpen() {
            if (closed) {
                throw new IllegalStateException("Statement 已关闭");
            }
        }
    }

    /** 模拟 {@code java.sql.PreparedStatement}：支持 ? 占位符、参数绑定、批处理。 */
    public static final class DemoPreparedStatement implements AutoCloseable {

        private final DemoConnection conn;
        private final String sql;
        private final Map<Integer, Object> params = new LinkedHashMap<>();
        private final List<Map<Integer, Object>> batch = new ArrayList<>();
        private boolean closed;

        DemoPreparedStatement(DemoConnection conn, String sql) {
            this.conn = conn;
            this.sql = sql;
        }

        public void setString(int index, String value) {
            params.put(index, value);
        }

        public void setInt(int index, int value) {
            params.put(index, value);
        }

        public DemoResultSet executeQuery() {
            checkOpen();
            return SqlEngine.query(conn.users(), sql, orderedParams());
        }

        public int executeUpdate() {
            checkOpen();
            return SqlEngine.update(conn.users(), sql, orderedParams(), conn.autoCommit);
        }

        /** 把当前参数组合入批，随后可继续 setXxx 绑定下一组。 */
        public void addBatch() {
            checkOpen();
            batch.add(new LinkedHashMap<>(params));
            params.clear();
        }

        /** 顺序执行批内全部参数组，返回每条的更新行数。 */
        public int[] executeBatch() {
            checkOpen();
            int[] counts = new int[batch.size()];
            for (int i = 0; i < batch.size(); i++) {
                counts[i] = SqlEngine.update(conn.users(), sql, toOrdered(batch.get(i)), conn.autoCommit);
            }
            batch.clear();
            return counts;
        }

        private Object[] orderedParams() {
            return toOrdered(params);
        }

        private static Object[] toOrdered(Map<Integer, Object> map) {
            Object[] arr = new Object[map.size()];
            for (Map.Entry<Integer, Object> e : map.entrySet()) {
                arr[e.getKey() - 1] = e.getValue();
            }
            return arr;
        }

        @Override
        public void close() {
            closed = true;
        }

        private void checkOpen() {
            if (closed) {
                throw new IllegalStateException("PreparedStatement 已关闭");
            }
        }
    }

    // ------------------------------------------------------------------
    // ResultSet / MetaData
    // ------------------------------------------------------------------

    /** 模拟 {@code java.sql.ResultSet}：游标从 -1 开始，next() 前进一行。 */
    public static final class DemoResultSet implements AutoCloseable {

        private final List<Map<String, Object>> rows;
        private final List<String> columns;
        private int cursor = -1;
        private boolean closed;

        DemoResultSet(List<Map<String, Object>> rows, List<String> columns) {
            this.rows = rows;
            this.columns = columns;
        }

        public boolean next() {
            checkOpen();
            return ++cursor < rows.size();
        }

        public String getString(String label) {
            checkOpen();
            Object v = current().get(label);
            return v == null ? null : String.valueOf(v);
        }

        public int getInt(String label) {
            checkOpen();
            Object v = current().get(label);
            return v == null ? 0 : ((Number) v).intValue();
        }

        public boolean wasNull() {
            checkOpen();
            return current().values().stream().anyMatch(v -> v == null);
        }

        public DemoResultSetMetaData getMetaData() {
            return new DemoResultSetMetaData(columns);
        }

        private Map<String, Object> current() {
            if (cursor < 0 || cursor >= rows.size()) {
                throw new IllegalStateException("游标不在有效行上，请先调用 next()");
            }
            return rows.get(cursor);
        }

        @Override
        public void close() {
            closed = true;
        }

        private void checkOpen() {
            if (closed) {
                throw new IllegalStateException("ResultSet 已关闭");
            }
        }
    }

    /** 模拟 {@code java.sql.ResultSetMetaData}。 */
    public static final class DemoResultSetMetaData {
        private final List<String> columns;

        DemoResultSetMetaData(List<String> columns) {
            this.columns = columns;
        }

        public int getColumnCount() {
            return columns.size();
        }

        public String getColumnName(int i) {
            return columns.get(i - 1);
        }
    }

    /** 模拟 {@code java.sql.DatabaseMetaData}。 */
    public static final class DemoDatabaseMetaData {
        public String getDatabaseProductName() {
            return "DemoDB（内存教学假库）";
        }

        public String getDriverName() {
            return "com.learn.jdbc.DemoDb";
        }

        public String getURL() {
            return "jdbc:demo:mem:learn";
        }
    }

    // ------------------------------------------------------------------
    // 极简 SQL 引擎：只识别教学用到的固定形态
    // ------------------------------------------------------------------

    static final class SqlEngine {

        static DemoResultSet query(List<Map<String, Object>> users, String sql, Object[] params) {
            String s = sql.trim();
            if (s.equalsIgnoreCase("SELECT * FROM users")) {
                return new DemoResultSet(new ArrayList<>(users), List.of("id", "name", "email"));
            }
            if (s.equalsIgnoreCase("SELECT * FROM users WHERE id = ?")) {
                int id = ((Number) params[0]).intValue();
                List<Map<String, Object>> hit = new ArrayList<>();
                for (Map<String, Object> u : users) {
                    if (((Number) u.get("id")).intValue() == id) {
                        hit.add(u);
                    }
                }
                return new DemoResultSet(hit, List.of("id", "name", "email"));
            }
            if (s.equalsIgnoreCase("SELECT * FROM users WHERE name = ?")) {
                String name = String.valueOf(params[0]);
                List<Map<String, Object>> hit = new ArrayList<>();
                for (Map<String, Object> u : users) {
                    if (name.equals(u.get("name"))) {
                        hit.add(u);
                    }
                }
                return new DemoResultSet(hit, List.of("id", "name", "email"));
            }
            throw new IllegalArgumentException("DemoDb 不认识这条查询 SQL: " + sql);
        }

        static int update(List<Map<String, Object>> users, String sql, Object[] params, boolean autoCommit) {
            String s = sql.trim();
            if (s.equalsIgnoreCase("INSERT INTO users (id, name, email) VALUES (?, ?, ?)")) {
                Map<String, Object> r = new LinkedHashMap<>();
                r.put("id", params[0]);
                r.put("name", params[1]);
                r.put("email", params[2]);
                users.add(r);
                return 1;
            }
            if (s.equalsIgnoreCase("UPDATE users SET name = ?, email = ? WHERE id = ?")) {
                int id = ((Number) params[2]).intValue();
                for (Map<String, Object> u : users) {
                    if (((Number) u.get("id")).intValue() == id) {
                        u.put("name", params[0]);
                        u.put("email", params[1]);
                        return 1;
                    }
                }
                return 0;
            }
            if (s.equalsIgnoreCase("DELETE FROM users WHERE id = ?")) {
                int id = ((Number) params[0]).intValue();
                return users.removeIf(u -> ((Number) u.get("id")).intValue() == id) ? 1 : 0;
            }
            throw new IllegalArgumentException("DemoDb 不认识这条更新 SQL: " + sql);
        }
    }
}
