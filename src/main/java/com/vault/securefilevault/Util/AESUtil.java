package com.vault.securefilevault.Util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class AESUtil {
    private static final String SECRET = "1234567890123456";

    public static byte[] encrypt(byte[] data) throws Exception{
        SecretKeySpec key = new SecretKeySpec(SECRET.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] encrypted) throws Exception{
        SecretKeySpec key = new SecretKeySpec(SECRET.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encrypted);
    }
}
