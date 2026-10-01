package com.learn.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 旧 java.io.File：exists / mkdirs / delete / 绝对相对路径。
 * 新代码推荐用 java.nio.file.Path + Files。
 */
public class FileClassDemo {

    public static void demo() {
        Path tmp = null;
        try {
            tmp = Files.createTempDirectory("io-file-");
            File base = tmp.toFile();

            File sub = new File(base, "logs/2024");   // 可一次建多层目录
            System.out.println("mkdirs 建目录: " + sub.mkdirs());
            System.out.println("exists 判断存在: " + sub.exists());
            System.out.println("是否目录: " + sub.isDirectory());

            File f = new File(base, "note.txt");
            Files.writeString(f.toPath(), "hello");
            System.out.println("文件名: " + f.getName() + ", 长度: " + f.length());
            System.out.println("绝对路径: " + f.getAbsolutePath());

            // 相对路径基于当前工作目录（user.dir），与 File 构造方式有关
            System.out.println("user.dir = " + System.getProperty("user.dir"));

            // 先在目录里放文件，再演示 delete 删非空目录失败（只返回 false，不抛异常）
            Files.writeString(new File(sub, "app.log").toPath(), "log");
            boolean deleted = sub.delete();
            System.out.println("delete 非空目录返回: " + deleted + "（失败易被忽略）");

            // 清掉内容后才能删掉目录
            System.out.println("目录还在？ " + sub.exists());
            new File(sub, "app.log").delete();
            System.out.println("删掉文件后再 delete: " + sub.delete());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            deleteDir(tmp);
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
