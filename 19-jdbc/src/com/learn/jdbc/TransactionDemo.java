package com.learn.jdbc;

import com.learn.jdbc.DemoDb.DemoConnection;
import com.learn.jdbc.DemoDb.DemoPreparedStatement;
import com.learn.jdbc.DemoDb.DemoResultSet;
import com.learn.jdbc.DemoDb.DemoSavepoint;

/**
 * 事务：setAutoCommit(false) / commit / rollback / 保存点。
 */
public class TransactionDemo {

    public static void demo() {
        try (DemoConnection conn = DemoDb.connect()) {
            System.out.println("初始数据:");
            printUsers(conn);

            // ---- 回滚演示：关闭自动提交，改完再 rollback ----
            conn.setAutoCommit(false);
            try (DemoPreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET name = ?, email = ? WHERE id = ?")) {
                ps.setString(1, "张三丰");
                ps.setString(2, "zsf@example.com");
                ps.setInt(3, 1);
                int n = ps.executeUpdate();
                System.out.println("事务内更新了 " + n + " 行");
            }
            System.out.println("事务内看数据（应看到张三丰）:");
            printUsers(conn);

            conn.rollback();
            System.out.println("rollback 之后（张三恢复）:");
            printUsers(conn);

            // ---- commit 演示 ----
            conn.setAutoCommit(false);
            try (DemoPreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (id, name, email) VALUES (?, ?, ?)")) {
                ps.setInt(1, 4);
                ps.setString(2, "赵六");
                ps.setString(3, "zhaoliu@example.com");
                ps.executeUpdate();
            }
            conn.commit();
            System.out.println("commit 之后（赵六保留）:");
            printUsers(conn);

            // ---- 保存点：只回滚到中间某一步 ----
            DemoSavepoint sp = conn.createSavepoint("before-delete");
            try (DemoPreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM users WHERE id = ?")) {
                ps.setInt(1, 2);
                ps.executeUpdate();
            }
            System.out.println("删除 id=2 之后:");
            printUsers(conn);

            conn.rollback(sp);
            System.out.println("rollback(保存点) 之后（李四回来了）:");
            printUsers(conn);
        }
    }

    private static void printUsers(DemoConnection conn) {
        try (DemoPreparedStatement ps = conn.prepareStatement("SELECT * FROM users");
             DemoResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                System.out.println("  " + rs.getInt("id") + " " + rs.getString("name"));
            }
        }
    }
}
