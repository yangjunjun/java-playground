package com.learn.crypto;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

import javax.crypto.Cipher;

/**
 * 非对称加密：RSA 密钥对、公钥加密/私钥解密、数字签名（Signature）。
 */
public class CipherAsymmetric {

    public static void demo() {
        try {
            // ---- 生成 RSA 密钥对（2048 位是当前下限）----
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair pair = kpg.generateKeyPair();
            PublicKey pub = pair.getPublic();
            PrivateKey priv = pair.getPrivate();
            System.out.println("RSA 密钥对已生成（2048 位）");
            System.out.println("公钥算法: " + pub.getAlgorithm() + ", 格式: " + pub.getFormat());

            // ---- 公钥加密 / 私钥解密 ----
            byte[] plain = "小数据：对称密钥的包裹传输".getBytes(StandardCharsets.UTF_8);

            Cipher enc = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            enc.init(Cipher.ENCRYPT_MODE, pub);
            byte[] cipherText = enc.doFinal(plain);
            System.out.println("公钥加密后 Base64: "
                    + Base64.getEncoder().encodeToString(cipherText).substring(0, 40) + "...");

            Cipher dec = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            dec.init(Cipher.DECRYPT_MODE, priv);
            byte[] recovered = dec.doFinal(cipherText);
            System.out.println("私钥解密: " + new String(recovered, StandardCharsets.UTF_8));

            // 实际工程：RSA 只用来“包裹”对称密钥（混合加密），
            // 大数据用 AES/GCM，小块密钥用 RSA 传输。
            System.out.println("要点：RSA 只适合加密小数据（受密钥长度限制），大数据用混合加密");

            // ---- 数字签名：私钥签名 / 公钥验签 ----
            byte[] msg = "合同内容 v1".getBytes(StandardCharsets.UTF_8);
            Signature signer = Signature.getInstance("SHA256withRSA");
            signer.initSign(priv);
            signer.update(msg);
            byte[] sig = signer.sign();
            System.out.println("签名 Base64: "
                    + Base64.getEncoder().encodeToString(sig).substring(0, 40) + "...");

            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(pub);
            verifier.update(msg);
            System.out.println("验签结果: " + verifier.verify(sig));

            // 篡改消息后验签失败
            verifier.initVerify(pub);
            verifier.update("合同内容 v2".getBytes(StandardCharsets.UTF_8));
            System.out.println("篡改后验签: " + verifier.verify(sig) + "（false 才是预期）");
            System.out.println("区别：加密保“机密性”，签名保“身份 + 完整性 + 不可否认”");
        } catch (Exception e) {
            System.out.println("非对称加密演示异常: " + e);
        }
    }
}
