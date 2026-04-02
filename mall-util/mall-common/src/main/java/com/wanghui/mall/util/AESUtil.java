package com.wanghui.mall.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;

public class AESUtil {


    /****
     * AES encryption/decryption
     * @param buffer: ciphertext/plaintext
     * @param appsecret: secret key
     * @param mode: encryption/decryption mode  1 encrypt  2 decrypt
     * @return
     */
    public static byte[] encryptAndDecrypt(byte[] buffer,String appsecret,Integer mode) throws Exception{
        // 1: Load encryption/decryption algorithm provider (includes algorithm, key management)
        Security.addProvider(new BouncyCastleProvider());

        // 2: Create secret key based on algorithm  1) key byte array  2) encryption algorithm
        SecretKeySpec secretKeySpec = new SecretKeySpec(appsecret.getBytes("UTF-8"),"AES");

        // 3: Set encryption mode (same for both encryption and decryption)
        // 1): AES/ECB/PKCS7Padding set algorithm
        // 2): Specify algorithm provider
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding","BC");
        // 4: Initialize encryption configuration
        cipher.init(mode,secretKeySpec);
        // 5: Execute encryption/decryption
        return cipher.doFinal(buffer);
    }

    /***
     * Encryption/decryption test
     * 128/192/256
     */
    public static void main(String[] args) throws Exception{
        String txt = "SpringCloud Alibaba";
        String appsecret="aaaaaaaaaaaaaaaa";
        appsecret = MD5.md5(appsecret);
        System.out.println(appsecret);
        Integer mode=1;

        // Encrypt
        byte[] bytes = encryptAndDecrypt(txt.getBytes("UTF-8"), appsecret, mode);
        String encode = Base64Util.encode(bytes);
        System.out.println(encode);

        // Decrypt -> Decode Base64 -> Decrypt
        byte[] decode = encryptAndDecrypt(Base64Util.decode(encode), appsecret, 2);
        System.out.println(new String(decode, "UTF-8"));

    }
}
