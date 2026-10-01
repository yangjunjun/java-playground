package com.learn.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * java.nio.file.Files：readString / writeString（Java 11+）、
 * readAllLines、copy、move、size 等一行式工具。
 */
public class NioFilesDemo {

    public static void demo() {
        Path dir = null;
        try {
            dir = Files.createTempDirectory("io-nio-");
            Path a = dir.resolve("a.txt");
            Path b = dir.resolve("b.txt");
            Path c = dir.resolve("c.txt");

            // writeString / readString：Java 11+
            Files.writeString(a, "行1\n行2\n行3", StandardCharsets.UTF_8);
            System.out.println("readString: " + Files.readString(a).replace("\n", " | "));

            // readAllLines：按行读成 List
            List<String> lines = Files.readAllLines(a, StandardCharsets.UTF_8);
            System.out.println("行数: " + lines.size() + ", 内容: " + lines);

            // copy / move / delete
            Files.copy(a, b, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("copy 后大小一致？ " + (Files.size(a) == Files.size(b)));

            Files.move(b, c, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("move 后 b 不存在？ " + !Files.exists(b)
                    + ", c 存在？ " + Files.exists(c));

            Files.deleteIfExists(c);
            System.out.println("delete 后 c 存在？ " + Files.exists(c));
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
