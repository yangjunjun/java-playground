package com.learn.crypto;

/**
 * 加密与安全学习总入口：依次运行各知识点示例。
 * 全部使用 JDK 自带 javax.crypto / java.security，无外部依赖、无网络。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. MessageDigest 摘要");
        MessageDigestDemo.demo();

        banner("2. HMAC 消息认证");
        MacDemo.demo();

        banner("3. 对称加密 AES");
        CipherSymmetric.demo();

        banner("4. 非对称加密 RSA 与数字签名");
        CipherAsymmetric.demo();

        banner("5. KeyStore 密钥库");
        KeyStoreBrief.demo();

        banner("6. SecureRandom 安全随机");
        SecureRandomDemo.demo();

        banner("7. 口令哈希 PBKDF2");
        PasswordHashingBrief.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
