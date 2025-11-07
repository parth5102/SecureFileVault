package com.vault.securefilevault.Util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * AES-256-GCM encryption utility.
 * Works with both environment variable AES_KEY_32 and application.properties fallback.
 * Layout: [IV(12)] [CIPHERTEXT+TAG]
 */
@Component
public class AESUtil {
    private static final String ALG = "AES";
    private static final String TRANS = "AES/GCM/NoPadding";
    private static final int GCM_TAG_BITS = 128;
    private static final int IV_LEN = 12;

    @Value("${AES_KEY_32:}")
    private String propertyKey;

    private static String staticKey;

    @PostConstruct
    public void init() {
        // Priority: environment variable → application.properties
        String envKey = System.getenv("AES_KEY_32");
        staticKey = (envKey != null && envKey.length() == 32) ? envKey : propertyKey;

        if (staticKey == null || staticKey.length() != 32) {
            throw new IllegalStateException("AES_KEY_32 must be a 32-character (256-bit) key.");
        }
    }

    private static byte[] key() {
        if (staticKey == null || staticKey.length() != 32)
            throw new IllegalStateException("AES_KEY_32 must be a 32-character (256-bit) key.");
        return staticKey.getBytes();
    }

    public static byte[] encrypt(byte[] plain) throws Exception {
        byte[] iv = new byte[IV_LEN];
        new SecureRandom().nextBytes(iv);
        Cipher cipher = Cipher.getInstance(TRANS);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key(), ALG), new GCMParameterSpec(GCM_TAG_BITS, iv));
        byte[] enc = cipher.doFinal(plain);
        byte[] out = new byte[IV_LEN + enc.length];
        System.arraycopy(iv, 0, out, 0, IV_LEN);
        System.arraycopy(enc, 0, out, IV_LEN, enc.length);
        return out;
    }

    public static byte[] decrypt(byte[] packed) throws Exception {
        byte[] iv = Arrays.copyOfRange(packed, 0, IV_LEN);
        byte[] enc = Arrays.copyOfRange(packed, IV_LEN, packed.length);
        Cipher cipher = Cipher.getInstance(TRANS);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key(), ALG), new GCMParameterSpec(GCM_TAG_BITS, iv));
        return cipher.doFinal(enc);
    }
}
