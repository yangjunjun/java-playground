# Cryptography and Security

Java provides cryptographic capabilities through `java.security` and `javax.crypto`: message digests, MAC, symmetric/asymmetric encryption, digital signatures, and secure randomness. This module is **pure JDK, with no external dependencies, and no network access**.

> **Iron rule: never invent your own cryptographic algorithms, and never "roll your own" composed cryptography.** Use standard APIs and standard modes, and leave key management to KeyStore / KMS.

## 1. MessageDigest: One-Way Hashing

```java
MessageDigest md = MessageDigest.getInstance("SHA-256");
byte[] hash = md.digest(data);          // always 32 bytes
```

| Algorithm | Status |
|------|------|
| SHA-256 / SHA-512 / SHA-3 | Available |
| MD5 | No longer secure (collision-prone); only for detecting file corruption |
| SHA-1 | No longer secure (actually collided in 2017) |

Use cases: file integrity checks, content addressing, fingerprinting; **do not use plain hashing for password storage** (see Section 7).

## 2. HMAC: Keyed Hashing

Used for message authentication (Webhook signature verification, JWT signing):

```java
Mac mac = Mac.getInstance("HmacSHA256");
mac.init(new SecretKeySpec(key, "HmacSHA256"));
byte[] tag = mac.doFinal(message);
// use MessageDigest.isEqual for constant-time comparison when verifying
```

## 3. Symmetric Encryption: AES

The recommended choice is **AES/GCM/NoPadding** (authenticated encryption, providing both confidentiality and integrity):

```java
Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv)); // random 12-byte iv
byte[] ct = c.doFinal(plain);
```

**The IV/nonce must be random and unique for every encryption** — never reuse it; change a single byte and GCM decryption fails (tamper protection).

## 4. Asymmetric Encryption: RSA and Digital Signatures

```java
KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
kpg.initialize(2048);
KeyPair pair = kpg.generateKeyPair();

// public-key encrypt / private-key decrypt (only for small data; use hybrid encryption for large data)
Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");

// private-key sign / public-key verify
Signature s = Signature.getInstance("SHA256withRSA");
s.initSign(priv); s.update(msg); byte[] sig = s.sign();
```

Encryption provides confidentiality; signatures provide identity + integrity + non-repudiation. In practice, RSA is used to "wrap" symmetric keys (hybrid encryption).

## 5. KeyStore: Key Container

```java
KeyStore ks = KeyStore.getInstance("PKCS12");
ks.load(null, null);
ks.setEntry("app-key", new SecretKeyEntry(secret), new PasswordProtection(pw));
```

The modern default is `PKCS12` (`JKS` is the legacy format). For production keys, prefer a KMS / Vault; never hard-code keys in source.

## 6. SecureRandom: Secure Randomness

```java
SecureRandom rng = new SecureRandom();   // OS CSPRNG
rng.nextBytes(iv);
```

`java.util.Random` is **predictable** (same seed, same sequence) and must never be used for tokens, IVs, or salts.

## 7. Password Hashing

Running `SHA-256(password)` directly is wrong: it is too fast and falls to GPU brute-force / rainbow-table lookup.

The JDK ships **PBKDF2WithHmacSHA256** (+ random salt + high iteration count):

```java
KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 120_000, 256);
SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
byte[] hash = f.generateSecret(spec).getEncoded();
```

For real projects, bcrypt / scrypt / argon2id are preferred (they require third-party libraries). Store the algorithm, iteration count, and salt alongside the hash to ease future migration.

## 8. Running This Module

**VS Code**: open `src/com/learn/crypto/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|------|
| `Main.java` | Main entry point; runs all examples in sequence |
| `MessageDigestDemo.java` | SHA-256 digest, MD5/SHA-1 insecurity notes |
| `MacDemo.java` | HMAC-SHA256 message authentication |
| `CipherSymmetric.java` | AES/GCM encryption/decryption and tamper protection |
| `CipherAsymmetric.java` | RSA key pair, encryption/decryption, digital signatures |
| `KeyStoreBrief.java` | Storing and retrieving keys in a PKCS12 KeyStore |
| `SecureRandomDemo.java` | Secure randomness vs java.util.Random |
| `PasswordHashingBrief.java` | Password hashing with PBKDF2WithHmacSHA256 |

## Common Pitfalls

1. Using MD5/SHA-1 for signatures or password hashing → already broken by collision/brute-force; forbidden in security contexts
2. Using ECB mode for AES or reusing the IV → plaintext patterns leak / GCM semantics break down
3. Generating tokens / IVs / salts with `java.util.Random` → predictable, equivalent to no encryption
4. Hashing passwords with plain SHA-256, no salt, no iterations → GPUs can reverse them in bulk
5. Designing your own cryptographic combinations (home-grown block modes, hand-rolled signatures) → high probability of vulnerabilities; use standard APIs
6. Hard-coding keys in source / config files → store them in a KeyStore / environment variables / KMS
