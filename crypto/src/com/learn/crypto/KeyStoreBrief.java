package com.learn.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.SecretKeyEntry;
import java.util.Enumeration;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * KeyStore 简介：密钥与证书的安全容器（JKS / PKCS12）。
 */
public class KeyStoreBrief {

    public static void demo() {
        try {
            // ---- 创建内存 KeyStore（PKCS12，现代默认格式）----
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(null, null); // 空 keystore
            System.out.println("KeyStore 类型: PKCS12（JKS 为旧格式，已不推荐）");

            // ---- 生成一把 AES 密钥并存入 ----
            SecretKey secret = KeyGenerator.getInstance("AES").generateKey();
            KeyStore.SecretKeyEntry entry = new SecretKeyEntry(secret);
            char[] password = "changeit".toCharArray();
            ks.setEntry("app-key", entry, new PasswordProtection(password));
            System.out.println("已存入密钥别名: app-key");

            // ---- 读取 ----
            KeyStore.SecretKeyEntry loaded =
                    (SecretKeyEntry) ks.getEntry("app-key", new PasswordProtection(password));
            System.out.println("读取到密钥算法: " + loaded.getSecretKey().getAlgorithm());

            // ---- 列出别名 ----
            Enumeration<String> aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                System.out.println("  别名: " + aliases.nextElement());
            }

            // ---- 序列化演示：真实项目会写入文件 / 从文件加载 ----
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ks.store(bos, password);
            System.out.println("keystore 字节数: " + bos.size());

            KeyStore ks2 = KeyStore.getInstance("PKCS12");
            ks2.load(new ByteArrayInputStream(bos.toByteArray()), password);
            System.out.println("重新加载后仍含 app-key: " + ks2.containsAlias("app-key"));

            // 真实用法（从文件加载）：
            //   KeyStore ks = KeyStore.getInstance("PKCS12");
            //   try (InputStream in = Files.newInputStream(Path.of("keystore.p12"))) {
            //       ks.load(in, "changeit".toCharArray());
            //   }
            System.out.println("要点：keystore 文件本身有密码保护；生产密钥建议放 KMS / Vault");
        } catch (Exception e) {
            System.out.println("KeyStore 演示异常: " + e);
        }
    }
}
