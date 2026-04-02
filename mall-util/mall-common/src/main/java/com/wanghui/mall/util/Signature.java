package com.wanghui.mall.util;
import com.alibaba.fastjson.JSON;

import java.util.*;

public class Signature {

    // Secret key
    private String skey;

    // Signature salt value
    private String salt;

    public Signature(String skey, String salt) {
        this.skey = skey;
        this.salt = salt;
    }

    /***
     * Decrypt ciphertext, convert to Map, and verify signature
     * @param ciphertext
     * @return
     */
    public Map<String,String> security(String ciphertext) throws Exception {
        // 1. Decrypt
        String decrypt =new String( AESUtil.encryptAndDecrypt(Base64Util.decodeURL(ciphertext), skey,2) , "UTF-8");
        // 2. Convert plaintext to Map and sort by key in descending order
        Map<String,String> decryptTreeMap = JSON.parseObject(decrypt,TreeMap.class);
        // 3. Verify signature
        String signature = decryptTreeMap.remove("signature");
        String localSignature = MD5.md5(JSON.toJSONString(decryptTreeMap),salt);
        // true: verification successful, false: verification failed
        return signature.equals(localSignature)? decryptTreeMap : null;
    }


    /***
     * Encrypt Map with signature
     */
    public String security(Map<String,String> dataMap) throws Exception {
        // 1. Convert dataMap to TreeMap
        dataMap = JSON.parseObject(JSON.toJSONString(dataMap),TreeMap.class);
        // 2. Convert TreeMap to JSON
        String treeJson = JSON.toJSONString(dataMap);
        // 3. Execute MD5 digest encryption
        String signature = MD5.md5(treeJson,salt);
        // 4. Add digest encryption content to dataMap
        dataMap.put("signature",signature);
        // 5. AES encrypt dataMap
        return Base64Util.encodeURL(AESUtil.encryptAndDecrypt(JSON.toJSONString(dataMap).getBytes("UTF-8"),skey,1));
    }


    public static void main(String[] args) throws Exception {
        String skey="ab2cc473d3334c39";
        String salt="XPYQZb1kMES8HNaJWW8+TDu/4JdBK4owsU9eXCXZDOI=";

        // String to be encrypted
        Map<String,String> dataMap = new HashMap<String,String>();
        dataMap.put("body", "商城订单-");
        dataMap.put("out_trade_no","AAA");
        dataMap.put("device_info", "PC");
        dataMap.put("fee_type", "CNY");
        dataMap.put("total_fee", "1");
        dataMap.put("spbill_create_ip","192.168.100.130");
        dataMap.put("notify_url", "http://www.example.com/wxpay/notify");
        dataMap.put("trade_type", "NATIVE");  // Native QR code payment

        Signature signature = new Signature(skey,salt);
        String cSrc = signature.security(dataMap);
        System.out.println(cSrc);
        Map<String, String> map1 = signature.security(cSrc);
        System.out.println(map1);

    }
}
