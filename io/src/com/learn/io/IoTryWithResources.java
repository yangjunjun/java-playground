package com.learn.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * try-with-resources：所有 Closeable/AutoCloseable 自动关闭，杜绝资源泄漏。
 */
public class IoTryWithResources {

    public static void demo() {
        Path dir = null;
        try {
            dir = Files.createTempDirectory("io-twr-");
            Path file = dir.resolve("data.txt");

            // 推荐写法：try-with-resources，无论正常/异常都会 close
            try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                bw.write("try-with-resources 安全关闭");
            }
            System.out.println("写入完成: " + Files.readString(file));

            // 反面教材（不要模仿）：手工 close 容易漏
            BufferedWriter leaked = null;
            try {
                leaked = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
                leaked.write("手工关闭写法");
            } finally {
                if (leaked != null) {
                    try {
                        leaked.close();
                    } catch (IOException e) {
                        // 忽略
                    }
                }
            }
            System.out.println("手工关闭写法也写入成功，但代码更啰嗦且易漏");

            // Java 9+ 可直接使用“已关闭资源”变量
            var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
            try (reader) {
                System.out.println("Java 9+ 语法读到: " + reader.readLine());
            }
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
