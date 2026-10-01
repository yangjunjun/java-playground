package com.learn.crypto;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Random;

/**
 * SecureRandom：密码学安全的随机数；对比 java.util.Random。
 */
public class SecureRandomDemo {

    public static void demo() {
        // ---- java.util.Random：可预测，绝不能用于安全场景 ----
        Random notSecure = new Random(42); // 同种子 → 同序列
        Random notSecure2 = new Random(42);
        System.out.println("java.util.Random 同种子可预测: "
                + (notSecure.nextInt() == notSecure2.nextInt())
                + "（攻击者猜到种子就能复现全部输出）");

        // ---- SecureRandom：操作系统 CSPRNG，不可预测 ----
        SecureRandom secure = new SecureRandom();
        byte[] token = new byte[32];
        secure.nextBytes(token);
        System.out.println("32 字节安全随机数 Base64: " + Base64.getEncoder().encodeToString(token));
        System.out.println("十六进制: " + HexFormat.of().formatHex(token).substring(0, 32) + "...");

        // 两次调用结果不同
        byte[] token2 = new byte[32];
        secure.nextBytes(token2);
        System.out.println("两次生成是否相同: " + java.util.Arrays.equals(token, token2));

        // ---- 常见用途 ----
        // - 会话 ID / CSRF Token / API Key
        // - 对称加密的 IV
        // - 口令加盐（salt）
        System.out.println("用途：会话 Token、加密 IV、口令盐 —— 都必须用 SecureRandom");

        // ---- 随机整数 / 边界 ----
        int pin = secure.nextInt(1_000_000); // [0, 1000000)
        System.out.println("随机 6 位 PIN 示例: " + String.format("%06d", pin));
        System.out.println("注意：new Random() 在需要安全的地方是 bug，不是小事");
    }
}
