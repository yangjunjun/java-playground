package com.learn.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 字符流：Reader/Writer、编码（StandardCharsets.UTF_8）、
 * BufferedReader/BufferedWriter 与 PrintWriter。
 */
public class CharStreamDemo {

    public static void demo() {
        Path dir = null;
        try {
            dir = Files.createTempDirectory("io-char-");
            Path textFile = dir.resolve("hello.txt");

            // 显式指定 UTF-8，避免平台默认编码乱码
            try (BufferedWriter bw = Files.newBufferedWriter(textFile, StandardCharsets.UTF_8)) {
                bw.write("第一行：你好，IO");
                bw.newLine();
                bw.write("第二行：Hello, Java");
            }

            // BufferedReader.readLine 逐行读
            try (BufferedReader br = Files.newBufferedReader(textFile, StandardCharsets.UTF_8)) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println("读到: " + line);
                }
            }

            // PrintWriter：println / printf，适合写文本日志
            Path logFile = dir.resolve("app.log");
            try (PrintWriter pw = new PrintWriter(
                    Files.newBufferedWriter(logFile, StandardCharsets.UTF_8))) {
                pw.println("INFO 启动成功");
                pw.printf("INFO 处理耗时 %d ms%n", 42);
            }
            System.out.println("日志内容: " + Files.readString(logFile));

            System.out.println("默认编码: " + System.getProperty("file.encoding")
                    + "，显式编码用 StandardCharsets.UTF_8 更安全");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            deleteDir(dir);
        }
    }

    private static void deleteDir(Path dir) {
        if (dir == null) {
            return;
        }
        try (var walk = Files.walk(dir)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException ignored) {
                    // 演示清理失败可忽略
                }
            });
        } catch (IOException ignored) {
            // 演示清理失败可忽略
        }
    }
}
