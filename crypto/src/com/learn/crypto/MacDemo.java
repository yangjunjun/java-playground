package com.learn.crypto;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * HMAC：带密钥的哈希，用于完整性校验与消息认证（如 JWT 签名、Webhook 验签）。
 */
public class MacDemo {

    public static void demo() {
        byte[] message = "order=1001&amount=99".getBytes(StandardCharsets.UTF_8);
        byte[] key = "demo-shared-secret".getBytes(StandardCharsets.UTF_8);

        // ---- HMAC-SHA256 ----
        byte[] mac1 = hmac("HmacSHA256", key, message);
        System.out.println("HMAC-SHA256: " + HexFormat.of().formatHex(mac1));
        System.out.println("Base64:      " + Base64.getEncoder().encodeToString(mac1));
        System.out.println("输出 32 字节: " + mac1.length);

        // 同一密钥 + 同一消息 → 结果可复现
        byte[] mac2 = hmac("HmacSHA256", key, message);
        System.out.println("结果可复现: " + MessageDigest.isEqual(mac1, mac2));

        // 密钥不同 → 验签失败
        byte[] otherKey = hmac("HmacSHA256", "wrong-secret".getBytes(StandardCharsets.UTF_8), message);
        System.out.println("错误密钥结果是否相同: " + MessageDigest.isEqual(mac1, otherKey));

        // ---- 可用算法 ----
        System.out.println("常用: HmacSHA256 / HmacSHA512；HmacMD5 仅兼容旧协议");
        System.out.println("验签必须用 MessageDigest.isEqual 常量时间比较，防止时序侧信道");

        // 真实用法（Webhook 验签）：
        //   String expected = Base64(hmac(secret, rawBody));
        //   if (!MessageDigest.isEqual(expected.getBytes(), headerSig.getBytes())) throw ...;
    }

    private static byte[] hmac(String algo, byte[] key, byte[] message) {
        try {
            Mac mac = Mac.getInstance(algo);
            mac.init(new SecretKeySpec(key, algo));
            return mac.doFinal(message);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException(algo + " 不可用", e);
        }
    }
}
