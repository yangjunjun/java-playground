# 加密与安全（Crypto）

Java 通过 `java.security` 与 `javax.crypto` 提供密码学能力：摘要、MAC、对称/非对称加密、数字签名、安全随机。本模块**纯 JDK、无外部依赖、不访问网络**。

> **铁律：不要自己发明加密算法，也不要自己“滚动实现”组合密码学。** 用标准 API、标准模式，密钥交给 KeyStore / KMS 管理。

## 1. MessageDigest：单向哈希

```java
MessageDigest md = MessageDigest.getInstance("SHA-256");
byte[] hash = md.digest(data);          // 固定 32 字节
```

| 算法 | 状态 |
|------|------|
| SHA-256 / SHA-512 / SHA-3 | 可用 |
| MD5 | 已不安全（易碰撞），只可用于校验文件损坏 |
| SHA-1 | 已不安全（2017 年被实际碰撞） |

用途：文件完整性校验、内容寻址、指纹；**口令存储不要用普通哈希**（见第 7 节）。

## 2. HMAC：带密钥的哈希

用于消息认证（Webhook 验签、JWT 签名）：

```java
Mac mac = Mac.getInstance("HmacSHA256");
mac.init(new SecretKeySpec(key, "HmacSHA256"));
byte[] tag = mac.doFinal(message);
// 验签用 MessageDigest.isEqual 常量时间比较
```

## 3. 对称加密：AES

推荐 **AES/GCM/NoPadding**（认证加密，同时保机密性与完整性）：

```java
Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv)); // iv 随机 12 字节
byte[] ct = c.doFinal(plain);
```

**IV/Nonce 每次加密必须随机且唯一**，绝不能复用；改一个字节 GCM 解密就会失败（防篡改）。

## 4. 非对称加密：RSA 与数字签名

```java
KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
kpg.initialize(2048);
KeyPair pair = kpg.generateKeyPair();

// 公钥加密 / 私钥解密（只适合小数据，大数据用混合加密）
Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");

// 私钥签名 / 公钥验签
Signature s = Signature.getInstance("SHA256withRSA");
s.initSign(priv); s.update(msg); byte[] sig = s.sign();
```

加密保机密性；签名保身份 + 完整性 + 不可否认。RSA 实际用途是“包裹”对称密钥（混合加密）。

## 5. KeyStore：密钥容器

```java
KeyStore ks = KeyStore.getInstance("PKCS12");
ks.load(null, null);
ks.setEntry("app-key", new SecretKeyEntry(secret), new PasswordProtection(pw));
```

现代默认 `PKCS12`（`JKS` 是旧格式）。生产密钥建议放 KMS / Vault，不要硬编码在代码里。

## 6. SecureRandom：安全随机

```java
SecureRandom rng = new SecureRandom();   // 操作系统 CSPRNG
rng.nextBytes(iv);
```

`java.util.Random` **可预测**（同种子同序列），绝不能用于 Token / IV / 盐。

## 7. 口令哈希

直接 `SHA-256(口令)` 是错的：太快，易被 GPU 暴力破解 / 彩虹表反查。

JDK 自带可用 **PBKDF2WithHmacSHA256**（+ 随机盐 + 高迭代次数）：

```java
KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 120_000, 256);
SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
byte[] hash = f.generateSecret(spec).getEncoded();
```

真实项目更推荐 bcrypt / scrypt / argon2id（需第三方库）。存储格式建议带上算法、迭代次数、盐，便于将来迁移。

## 8. 运行本模块

**VS Code**：打开 `src/com/learn/crypto/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `MessageDigestDemo.java` | SHA-256 摘要、MD5/SHA-1 不安全说明 |
| `MacDemo.java` | HMAC-SHA256 消息认证 |
| `CipherSymmetric.java` | AES/GCM 加解密与防篡改 |
| `CipherAsymmetric.java` | RSA 密钥对、加解密、数字签名 |
| `KeyStoreBrief.java` | PKCS12 KeyStore 存取密钥 |
| `SecureRandomDemo.java` | 安全随机 vs java.util.Random |
| `PasswordHashingBrief.java` | PBKDF2WithHmacSHA256 口令哈希 |

## 易错点

1. 用 MD5/SHA-1 做签名或口令哈希 → 已被碰撞/暴力破解，安全场景禁用
2. AES 用 ECB 模式或复用 IV → 明文模式泄露 / GCM 语义崩坏
3. `java.util.Random` 生成 Token / IV / 盐 → 可预测，等于没加密
4. 口令直接 SHA-256 不加盐不迭代 → GPU 可批量反查
5. 自己“设计”加密组合（自制分组模式、自制签名）→ 高概率有漏洞，用标准 API
6. 密钥硬编码在源码 / 配置文件里 → 应放 KeyStore / 环境变量 / KMS
