package com.wanghui.mall.util;

import org.apache.commons.codec.digest.DigestUtils;
public class MD5 {

    /**
     * MD5 method
     * @param text plaintext
     * @return ciphertext
     * @throws Exception
     */
    public static String md5(String text) throws Exception {
        // Encoded string
        String encode= DigestUtils.md5Hex(text);
        return encode;
    }

    /**
     * MD5 method
     * @param text plaintext
     * @param key salt
     * @return ciphertext
     * @throws Exception
     */
    public static String md5(String text, String key) throws Exception {
        // Encoded string
        String encode= DigestUtils.md5Hex(text + key);
        return encode;
    }

    /**
     * MD5 verification method
     * @param text plaintext
     * @param key key/salt
     * @param md5 ciphertext
     * @return true/false
     * @throws Exception
     */
    public static boolean verify(String text, String key, String md5) throws Exception {
        // Verify with provided key
        String md5Text = md5(text, key);
        return md5Text.equalsIgnoreCase(md5);
    }
}
