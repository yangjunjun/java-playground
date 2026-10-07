package com.learn.jdbc;

import com.learn.jdbc.DemoDb.DemoConnection;
import com.learn.jdbc.DemoDb.DemoDatabaseMetaData;
import com.learn.jdbc.DemoDb.DemoPreparedStatement;
import com.learn.jdbc.DemoDb.DemoResultSet;

/**
 * DatabaseMetaData 与批处理（batch update）。
 */
public class MetadataAndBatch {

    public static void demo() {
        try (DemoConnection conn = DemoDb.connect()) {
            // ---- 元数据 ----
            DemoDatabaseMetaData meta = conn.getMetaData();
            System.out.println("产品名: " + meta.getDatabaseProductName());
            System.out.println("驱动名: " + meta.getDriverName());
            System.out.println("URL: " + meta.getURL());
            System.out.println("（真实 JDBC 还能查表名、列类型、主外键：DatabaseMetaData.getTables 等）");

            // ---- 批处理：一次往返提交多条 SQL ----
            try (DemoPreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (id, name, email) VALUES (?, ?, ?)")) {
                String[][] rows = {
                        { "4", "赵六", "zhaoliu@example.com" },
                        { "5", "孙七", "sunqi@example.com" },
                        { "6", "周八", "zhouba@example.com" },
                };
                for (String[] r : rows) {
                    ps.setInt(1, Integer.parseInt(r[0]));
                    ps.setString(2, r[1]);
                    ps.setString(3, r[2]);
                    ps.addBatch(); // 绑定完一组就入批
                }
                int[] counts = ps.executeBatch();
                System.out.println("批处理插入行数: " + java.util.Arrays.toString(counts));
            }

            try (DemoPreparedStatement ps = conn.prepareStatement("SELECT * FROM users");
                 DemoResultSet rs = ps.executeQuery()) {
                System.out.println("批处理后的全表:");
                while (rs.next()) {
                    System.out.println("  " + rs.getInt("id") + " " + rs.getString("name"));
                }
            }
        }
    }
}
