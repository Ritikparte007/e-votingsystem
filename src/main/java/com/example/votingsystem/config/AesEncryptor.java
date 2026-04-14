package com.example.votingsystem.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * AesEncryptor — Sensitive fields ko AES-256-CBC se encrypt/decrypt karta hai.
 *
 * Kaise kaam karta hai:
 *   - Jab data DB mein SAVE hota hai → convertToDatabaseColumn() → encrypt
 *   - Jab data DB se READ hota hai  → convertToEntityAttribute() → decrypt
 *
 * application.properties mein add karo:
 *   app.encryption.key=YourSecretKey1234567890123456  (exactly 32 chars = 256-bit)
 *   app.encryption.iv=YourIV12345678                  (exactly 16 chars)
 */
@Converter
@Component
public class AesEncryptor implements AttributeConverter<String, String> {

    // ─── Algorithm ────────────────────────────────────────────────────────────
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    // ─── Key & IV (application.properties se inject hoga) ────────────────────
    // Static fields isliye kyunki JPA Converter ko Spring inject nahi kar sakta directly
    private static String encryptionKey;
    private static String encryptionIv;

    // ─── Spring inject karega, phir static mein copy karenge ─────────────────
    @Value("${app.encryption.key}")
    public void setEncryptionKey(String key) {
        AesEncryptor.encryptionKey = key;
    }

    @Value("${app.encryption.iv}")
    public void setEncryptionIv(String iv) {
        AesEncryptor.encryptionIv = iv;
    }

    // ─── Encrypt: Java String → DB Column (Base64 encoded ciphertext) ─────────
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(encryptionIv.getBytes("UTF-8"));

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encrypted = cipher.doFinal(attribute.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encrypted); // DB mein ye store hoga
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed for field!", e);
        }
    }

    // ─── Decrypt: DB Column (Base64) → Java String ────────────────────────────
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(encryptionIv.getBytes("UTF-8"));

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decoded = Base64.getDecoder().decode(dbData);
            return new String(cipher.doFinal(decoded), "UTF-8"); // Original value milega
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed for field!", e);
        }
    }
}