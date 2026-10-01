package com.learn.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * NIO 缓冲区与通道：ByteBuffer + FileChannel，以及 transferTo 高效拷贝。
 */
public class BufferChannelDemo {

    public static void demo() {
        Path dir = null;
        try {
            dir = Files.createTempDirectory("io-buf-");
            Path src = dir.resolve("src.txt");
            Path dst = dir.resolve("dst.txt");

            Files.writeString(src, "Hello, FileChannel!", StandardCharsets.UTF_8);

            // ByteBuffer：position / limit / capacity
            ByteBuffer buf = ByteBuffer.allocate(16);
            System.out.println("新缓冲区 capacity=" + buf.capacity()
                    + ", position=" + buf.position() + ", limit=" + buf.limit());

            // 用通道读入缓冲区，flip 后再读出
            try (FileChannel channel = FileChannel.open(src, StandardOpenOption.READ)) {
                int read = channel.read(buf);
                System.out.println("读入字节数: " + read);
                buf.flip(); // 写模式 -> 读模式
                byte[] out = new byte[buf.remaining()];
                buf.get(out);
                System.out.println("缓冲区内容: " + new String(out, StandardCharsets.UTF_8));
            }

            // transferTo：通道间直接拷贝（大文件高效）
            try (FileChannel in = FileChannel.open(src, StandardOpenOption.READ);
                 FileChannel out = FileChannel.open(dst,
                         StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                long transferred = in.transferTo(0, in.size(), out);
                System.out.println("transferTo 传输字节数: " + transferred);
            }

            System.out.println("拷贝结果: " + Files.readString(dst));
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
