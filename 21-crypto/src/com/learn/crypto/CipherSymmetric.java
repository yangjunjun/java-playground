package com.learn.crypto;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 对称加密：AES。推荐 AES/GCM（认证加密，同时保证机密性与完整性）。
 */
public class CipherSymmetric {

    /** GCM 常用参数：128 位认证标签 + 12 字节随机 IV。 */
    private static final int GCM_TAG_BITS = 128;
    private static final int GCM_IV_BYTES = 12;

    public static void demo() {
        try {
            byte[] plain = "机密数据：账户余额 10000".getBytes(StandardCharsets.UTF_8);

            // ---- 密钥：AES-256 需要 32 字节密钥 ----
            byte[] keyBytes = new byte[32];
            new SecureRandom().nextBytes(keyBytes);
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            System.out.println("AES 密钥长度: " + keyBytes.length + " 字节 (256 位)");

            // ---- AES/GCM 加密：每次加密必须用新的随机 IV ----
            byte[] iv = new byte[GCM_IV_BYTES];
            new SecureRandom().nextBytes(iv);

            Cipher enc = Cipher.getInstance("AES/GCM/NoPadding");
            enc.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] cipherText = enc.doFinal(plain);
            System.out.println("密文 Base64: " + Base64.getEncoder().encodeToString(cipherText));
            System.out.println("密文长度 = 明文 + 16 字节认证标签: " + cipherText.length + " 字节");

            // ---- 解密：必须提供同一密钥 + 同一 IV ----
            Cipher dec = Cipher.getInstance("AES/GCM/NoPadding");
            dec.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] recovered = dec.doFinal(cipherText);
            System.out.println("解密结果: " + new String(recovered, StandardCharsets.UTF_8));
            System.out.println("还原一致: " + java.util.Arrays.equals(plain, recovered));

            // ---- GCM 的防篡改：改一个字节就解密失败 ----
            byte[] tampered = cipherText.clone();
            tampered[0] ^= 0x01;
            try {
                dec.doFinal(tampered);
                System.out.println("意外：篡改密文也能解密！");
            } catch (Exception e) {
                System.out.println("篡改密文后解密失败（预期）: " + e.getClass().getSimpleName()
                        + " —— GCM 提供完整性保护");
            }

            // ---- 实用要点 ----
            System.out.println("要点 1: IV/Nonce 每次加密必须随机且唯一，绝不能复用同一 IV 加密多条消息");
            System.out.println("要点 2: 密钥与密文分开存储；密钥可用 KeyStore / KMS 管理");
            System.out.println("要点 3: 不要用 ECB 模式；优先 AES/GCM/NoPadding");
        } catch (Exception e) {
            System.out.println("对称加密演示异常: " + e);
        }
    }
}
