package com.learn.crypto;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * 口令哈希：为什么不能直接 SHA-256，以及 PBKDF2 的正确姿势。
 * <p>
 * 真实项目推荐 bcrypt / scrypt / argon2（需第三方库）；
 * JDK 自带可用 <b>PBKDF2WithHmacSHA256</b>。
 */
public class PasswordHashingBrief {

    private static final int SALT_BYTES = 16;
    private static final int ITERATIONS = 120_000;
    private static final int KEY_BITS = 256;

    public static void demo() {
        try {
            String password = "correct-horse-battery-staple";

            // ---- 错误示范：直接哈希口令 ----
            // SHA-256 太快，且彩虹表可批量反查；加盐也挡不住 GPU 暴力破解
            System.out.println("错误示范: SHA-256(口令) 太快，易被暴力/彩虹表破解");

            // ---- 正确示范：PBKDF2WithHmacSHA256 + 随机盐 + 高迭代次数 ----
            byte[] salt = new byte[SALT_BYTES];
            new SecureRandom().nextBytes(salt);

            byte[] hash = pbkdf2(password, salt, ITERATIONS);
            System.out.println("PBKDF2 迭代次数: " + ITERATIONS);
            System.out.println("盐 Base64:  " + Base64.getEncoder().encodeToString(salt));
            System.out.println("哈希 Base64: " + Base64.getEncoder().encodeToString(hash));
            System.out.println("输出长度: " + hash.length + " 字节");

            // ---- 校验：用同一盐与迭代次数重算，常量时间比较 ----
            byte[] again = pbkdf2(password, salt, ITERATIONS);
            boolean ok = java.security.MessageDigest.isEqual(hash, again);
            System.out.println("口令校验（正确口令）: " + ok);

            byte[] wrong = pbkdf2("wrong-password", salt, ITERATIONS);
            System.out.println("口令校验（错误口令）: "
                    + java.security.MessageDigest.isEqual(hash, wrong));

            // ---- 存储格式建议：把算法、迭代次数、盐、哈希一起存 ----
            System.out.println("存储格式示例: pbkdf2-sha256$" + ITERATIONS + "$"
                    + Base64.getEncoder().encodeToString(salt) + "$"
                    + Base64.getEncoder().encodeToString(hash));
            System.out.println("这样将来提高迭代次数时可以做迁移校验");

            // ---- 生产建议 ----
            System.out.println("生产优先: bcrypt / scrypt / argon2id（需依赖库），或 JDK 的 PBKDF2");
            System.out.println("永远不要自己发明口令哈希算法，也不要滚动实现加密");
        } catch (Exception e) {
            System.out.println("口令哈希演示异常: " + e);
        }
    }

    private static byte[] pbkdf2(String password, byte[] salt, int iterations) throws Exception {
        KeySpec spec = new PBEKeySpec(
                password.toCharArray(), salt, iterations, KEY_BITS);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec).getEncoded();
    }
}
