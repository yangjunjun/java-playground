package com.learn.jdbc;

import com.learn.jdbc.DemoDb.DemoConnection;
import com.learn.jdbc.DemoDb.DemoPreparedStatement;
import com.learn.jdbc.DemoDb.DemoResultSet;

/**
 * PreparedStatement：? 占位符、参数绑定，以及与字符串拼接的 SQL 注入对比。
 */
public class PreparedStatementDemo {

    public static void demo() {
        try (DemoConnection conn = DemoDb.connect()) {
            // ---- 正确姿势：PreparedStatement + ? ----
            try (DemoPreparedStatement ps =
                         conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
                ps.setInt(1, 2);
                try (DemoResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("按 id 查询到: " + rs.getString("name"));
                    }
                }
            }

            try (DemoPreparedStatement ps =
                         conn.prepareStatement("SELECT * FROM users WHERE name = ?")) {
                ps.setString(1, "张三");
                try (DemoResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("按 name 查询到: " + rs.getString("email"));
                    }
                }
            }
        }

        // ---- 错误姿势：字符串拼接 SQL（SQL 注入）----
        // 下面只是“文本演示”，不真的执行注入，但拼出来的 SQL 一眼就能看出危险。
        String userInput = "' OR '1'='1"; // 攻击者输入
        String dangerousSql = "SELECT * FROM users WHERE name = '" + userInput + "'";
        System.out.println("拼接得到的 SQL: " + dangerousSql);
        System.out.println("→ WHERE 条件被篡改成恒真，会拖出全表！");
        System.out.println("对比 PreparedStatement：驱动把参数当数据传输，不当 SQL 解析");
        System.out.println("结论：任何来自用户的值都必须走 setXxx(? 绑定)，禁止拼进 SQL 文本");
    }
}
