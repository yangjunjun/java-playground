package com.learn.jdbc;

import com.learn.jdbc.DemoDb.DemoConnection;
import com.learn.jdbc.DemoDb.DemoPreparedStatement;
import com.learn.jdbc.DemoDb.DemoResultSet;
import com.learn.jdbc.DemoDb.DemoResultSetMetaData;

/**
 * ResultSet 迭代与元数据：next()/getXxx/wasNull/列名与列号。
 */
public class ResultSetDemo {

    public static void demo() {
        try (DemoConnection conn = DemoDb.connect();
             DemoPreparedStatement ps = conn.prepareStatement("SELECT * FROM users");
             DemoResultSet rs = ps.executeQuery()) {

            // 列元数据：先看清有哪些列
            DemoResultSetMetaData md = rs.getMetaData();
            System.out.println("列数 = " + md.getColumnCount());
            for (int i = 1; i <= md.getColumnCount(); i++) {
                System.out.println("  第 " + i + " 列: " + md.getColumnName(i));
            }

            // 标准遍历模式：while (rs.next()) { ... }
            System.out.println("按列名取值:");
            while (rs.next()) {
                System.out.println("  name=" + rs.getString("name")
                        + ", email=" + rs.getString("email"));
            }
        }

        // 注意游标语义：next() 之前不能取值；关了 ResultSet 之后也不能再用
        System.out.println("提示：ResultSet 是游标模型，next() 返回 false 表示没有更多行");
        System.out.println("提示：getXxx(列名) 可读性好；批量导出可用 getXxx(列号) 略快");
    }
}
