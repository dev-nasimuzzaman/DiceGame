package com.dicegame;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class HMACGenerator {
    private static final String HMAC_ALGO = "HmacSHA256";

    public String generateHMAC(String key, String message) throws Exception {
        Mac mac = Mac.getInstance(HMAC_ALGO);
        mac.init(new SecretKeySpec(key.getBytes(), HMAC_ALGO));
        byte[] hmac = mac.doFinal(message.getBytes());
        return bytesToHex(hmac);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String generateRandomKey() {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[32];
        random.nextBytes(key);
        return bytesToHex(key);
    }
}