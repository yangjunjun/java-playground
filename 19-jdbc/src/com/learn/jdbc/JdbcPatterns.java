package com.learn.jdbc;

import com.learn.jdbc.DemoDb.DemoConnection;
import com.learn.jdbc.DemoDb.DemoPreparedStatement;
import com.learn.jdbc.DemoDb.DemoResultSet;

/**
 * JDBC 实用模式：RowSet 简介、连接池概念、资源关闭与常见坑。
 */
public class JdbcPatterns {

    public static void demo() {
        // ---- 模式 1：DAO 分层（把 JDBC 调用收进数据访问层）----
        UserDao dao = new UserDao();
        try (DemoConnection conn = DemoDb.connect()) {
            System.out.println("DAO 查询 id=3: " + dao.findNameById(conn, 3));
        }

        // ---- 模式 2：RowSet（断开连接的行集）----
        // javax.sql.rowset.CachedRowSet 可在离线时持有结果，再“倒回”数据库。
        // 本仓库不连真实库，仅说明用法（不执行）：
        //   CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
        //   crs.populate(resultSet);  // 填充后可关闭连接，稍后再用 crs 做更新
        System.out.println("RowSet：CachedRowSet 支持断开式结果集，适合离线编辑再同步");

        // ---- 模式 3：连接池概念 ----
        // 频繁 DriverManager.getConnection 开销大；生产用 HikariCP / Druid / DBCP
        // 池化复用 Connection，关键配置：最大连接数、空闲超时、泄漏检测。
        // 伪代码（不执行）：
        //   HikariConfig cfg = new HikariConfig();
        //   cfg.setJdbcUrl("jdbc:mysql://localhost:3306/demo");
        //   cfg.setMaximumPoolSize(10);
        //   DataSource ds = new HikariDataSource(cfg);
        //   try (Connection conn = ds.getConnection()) { ... }
        System.out.println("连接池：生产环境用 HikariCP 等管理 Connection 复用，不要每次新建");

        // ---- 模式 4：异常处理与资源关闭 ----
        // JDBC 受检异常 SQLException；close 本身也可能抛异常
        // try-with-resources 能保证“后开先关”，且 close 异常会被抑制（suppressed）
        System.out.println("关闭顺序：ResultSet → Statement → Connection，try-with-resources 自动按逆序关");
    }

    /** 简单 DAO 示例：把 SQL 与业务代码隔离。 */
    static final class UserDao {

        String findNameById(DemoConnection conn, int id) {
            try (DemoPreparedStatement ps =
                         conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
                ps.setInt(1, id);
                try (DemoResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getString("name") : null;
                }
            }
        }
    }
}
