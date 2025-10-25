package com.vault.securefilevault.Util;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * AES-256-GCM with random 12-byte IV, tag 128 bits
 * Layout: [IV(12)] [CIPHERTEXT+TAG]
 */
public class AESUtil {
    private static final String ALG = "AES";
    private static final String TRANS = "AES/GCM/NoPadding";
    private static final int GCM_TAG_BITS = 128;
    private static final int IV_LEN = 12;

    // DEMO ONLY: derive key from env var; in production use a proper KMS/Secrets Manager.
    private static byte[] key() {
        String env = System.getenv().getOrDefault("AES_KEY_32", "");
        if (env.length() != 32) throw new IllegalStateException("AES_KEY_32 must be 32 chars (256-bit key)");
        return env.getBytes();
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
