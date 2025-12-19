package com.newbee.ble_lib.util;



import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class CryptoUtils {


    public static byte[] getMD5(byte[] data) {
        try {
            // 使用MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(data);
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
