package com.learn.io;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

/**
 * Path / Paths.get 与目录遍历：Files.walk、Files.walkFileTree、FileVisitor。
 */
public class PathWalkDemo {

    public static void demo() {
        Path dir = null;
        try {
            dir = Files.createTempDirectory("io-walk-");
            final Path root = dir;

            // 构造 Path：Java 8 用 Paths.get，Java 11+ 用 Path.of
            Path sub = root.resolve("sub");
            Files.createDirectories(sub);
            Files.writeString(root.resolve("root.txt"), "root");
            Files.writeString(sub.resolve("child.txt"), "child");

            Path p = Paths.get(root.toString(), "sub", "child.txt");
            System.out.println("文件名: " + p.getFileName());
            System.out.println("父目录: " + p.getParent().getFileName());
            System.out.println("绝对路径存在？ " + Files.exists(p.toAbsolutePath()));

            // Files.walk：惰性 Stream，用完必须 close
            System.out.println("-- Files.walk --");
            try (Stream<Path> walk = Files.walk(root)) {
                walk.forEach(path -> System.out.println("  " + root.relativize(path)));
            }

            // walkFileTree + FileVisitor：回调式遍历，可提前终止
            System.out.println("-- walkFileTree --");
            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    System.out.println("  访问文件: " + file.getFileName()
                            + " (" + attrs.size() + " 字节)");
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path d, BasicFileAttributes attrs) {
                    if (d.equals(root)) {
                        return FileVisitResult.CONTINUE;
                    }
                    System.out.println("  进入目录: " + d.getFileName());
                    return FileVisitResult.CONTINUE;
                }
            });
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
