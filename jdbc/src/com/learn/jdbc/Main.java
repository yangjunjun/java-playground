package com.learn.jdbc;

/**
 * JDBC 学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. JDBC 基础与架构");
        JdbcBasics.demo();

        banner("2. PreparedStatement 与 SQL 注入");
        PreparedStatementDemo.demo();

        banner("3. 事务：提交 / 回滚 / 保存点");
        TransactionDemo.demo();

        banner("4. ResultSet 遍历");
        ResultSetDemo.demo();

        banner("5. 元数据与批处理");
        MetadataAndBatch.demo();

        banner("6. 实用模式：DAO / RowSet / 连接池");
        JdbcPatterns.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
