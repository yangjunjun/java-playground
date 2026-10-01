package com.learn.jdbc;

import com.learn.jdbc.DemoDb.DemoConnection;
import com.learn.jdbc.DemoDb.DemoResultSet;
import com.learn.jdbc.DemoDb.DemoStatement;

/**
 * JDBC 基础：架构分层、Statement 查询、try-with-resources 关闭资源。
 */
public class JdbcBasics {

    public static void demo() {
        // 真实 JDBC 架构（本仓库用 DemoDb 模拟同一套调用形态）：
        //   DriverManager → Connection → Statement/PreparedStatement → ResultSet
        //
        // 真实项目写法（需要驱动 jar，此处不执行）：
        //   Connection conn = DriverManager.getConnection(
        //       "jdbc:mysql://localhost:3306/demo", "root", "secret");
        //   // 或 JDBC 4.0+ 自动加载驱动；旧写法 Class.forName("com.mysql.cj.jdbc.Driver");

        // try-with-resources：无论正常结束还是异常，都会自动 close
        try (DemoConnection conn = DemoDb.connect();
             DemoStatement st = conn.createStatement();
             DemoResultSet rs = st.executeQuery("SELECT * FROM users")) {

            System.out.println("已获取“连接”，自动提交 = " + conn.getAutoCommit());
            System.out.println("遍历 ResultSet：");
            while (rs.next()) {
                System.out.println("  id=" + rs.getInt("id")
                        + ", name=" + rs.getString("name")
                        + ", email=" + rs.getString("email"));
            }
        }

        System.out.println("try-with-resources 结束，连接/Statement/ResultSet 均已自动关闭");
    }
}
