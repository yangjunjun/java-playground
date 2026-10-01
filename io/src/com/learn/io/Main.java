package com.learn.io;

/**
 * IO 与 NIO 学习总入口：依次运行各知识点示例。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. 字节流 InputStream / OutputStream");
        ByteStreamDemo.demo();

        banner("2. 字符流与编码");
        CharStreamDemo.demo();

        banner("3. File 类基本操作");
        FileClassDemo.demo();

        banner("4. java.nio.file.Files 常用方法");
        NioFilesDemo.demo();

        banner("5. Path 与目录遍历");
        PathWalkDemo.demo();

        banner("6. ByteBuffer 与 FileChannel");
        BufferChannelDemo.demo();

        banner("7. try-with-resources 与资源关闭");
        IoTryWithResources.demo();

        banner("8. 对象序列化简要");
        ObjectStreamBrief.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
