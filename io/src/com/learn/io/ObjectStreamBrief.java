package com.learn.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 对象序列化简要：ObjectOutputStream / ObjectInputStream + Serializable。
 * 仅作了解；新项目优先 JSON 等显式格式（序列化有兼容与安全隐患）。
 */
public class ObjectStreamBrief {

    /** 需要序列化的类必须实现 Serializable */
    static class User implements Serializable {
        private static final long serialVersionUID = 1L;

        String name;
        int age;

        User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{name='" + name + "', age=" + age + "}";
        }
    }

    public static void demo() {
        Path dir = null;
        try {
            dir = Files.createTempDirectory("io-ser-");
            Path file = dir.resolve("users.dat");

            List<User> users = List.of(new User("张三", 20), new User("李四", 25));

            // 写出对象（注意：List.of 的实现类可序列化，这里拷贝到 ArrayList 更典型）
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file))) {
                oos.writeObject(new ArrayList<>(users));
            }

            // 读回对象
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file))) {
                @SuppressWarnings("unchecked")
                List<User> back = (List<User>) ois.readObject();
                System.out.println("反序列化结果: " + back);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }

            System.out.println("提示: 序列化存在版本兼容与反序列化安全风险，生产环境更推荐 JSON");
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
