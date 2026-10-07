package com.learn.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 字节流：InputStream / OutputStream 层次与基本读写。
 * 适合二进制数据（图片、音频、序列化字节）。
 */
public class ByteStreamDemo {

    public static void demo() {
        Path dir = null;
        try {
            dir = Files.createTempDirectory("io-byte-");
            Path src = dir.resolve("src.bin");
            Path dst = dir.resolve("dst.bin");

            // 写字节
            byte[] data = { 72, 101, 108, 108, 111, 33 }; // "Hello!"
            try (OutputStream out = new FileOutputStream(src.toFile())) {
                out.write(data);
            }

            // 读字节（readAllBytes 是 Java 9+；逐字节 read() 是经典写法）
            try (InputStream in = new FileInputStream(src.toFile())) {
                int b;
                while ((b = in.read()) != -1) {
                    System.out.print((char) b);
                }
                System.out.println(" <- 逐字节读完");
            }

            // 整段拷贝：transferTo (Java 9+)
            try (InputStream in = new FileInputStream(src.toFile());
                 OutputStream out = new FileOutputStream(dst.toFile())) {
                long copied = in.transferTo(out);
                System.out.println("transferTo 拷贝字节数: " + copied);
            }

            System.out.println("内容一致？ " + java.util.Arrays.equals(
                    Files.readAllBytes(src), Files.readAllBytes(dst)));
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
