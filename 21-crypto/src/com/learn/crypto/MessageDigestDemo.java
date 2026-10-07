package com.learn.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;

/**
 * MessageDigest：单向哈希（摘要）。SHA-256 为主，说明 MD5/SHA-1 的不安全性。
 */
public class MessageDigestDemo {

    public static void demo() {
        byte[] data = "你好，Java 安全！".getBytes(StandardCharsets.UTF_8);

        // ---- SHA-256：目前通用的摘要算法 ----
        byte[] sha256 = digest("SHA-256", data);
        System.out.println("SHA-256 十六进制: " + HexFormat.of().formatHex(sha256));
        System.out.println("SHA-256 Base64:   " + Base64.getEncoder().encodeToString(sha256));
        System.out.println("输出固定 32 字节 = 256 位: " + sha256.length + " 字节");

        // ---- 同一输入，摘要恒定 ----
        byte[] again = digest("SHA-256", data);
        System.out.println("重复计算结果一致: " + MessageDigest.isEqual(sha256, again));

        // ---- 不同输入，摘要完全不同（雪崩效应）----
        byte[] other = digest("SHA-256", "你好，Java 安全？".getBytes(StandardCharsets.UTF_8));
        System.out.println("改一个标点后是否相同: " + MessageDigest.isEqual(sha256, other));

        // ---- MD5 / SHA-1：已不安全，仅供识别旧系统 ----
        System.out.println("MD5   长度: " + digest("MD5", data).length + " 字节（易碰撞，勿用于签名/密码）");
        System.out.println("SHA-1 长度: " + digest("SHA-1", data).length + " 字节（2017 年已被实际碰撞）");
        System.out.println("结论：新代码用 SHA-256 / SHA-3；MD5、SHA-1 只可用于校验文件损坏，不可用于安全场景");

        // ---- 常见用途：口令存储应使用盐 + 慢哈希（见 PasswordHashingBrief）----
        //   文件完整性校验、内容寻址、去重指纹 → MessageDigest 足够
    }

    private static byte[] digest(String algo, byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance(algo);
            return md.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(algo + " 不可用", e);
        }
    }
}
