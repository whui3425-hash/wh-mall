package com.gupaoedu.vip.mall.pay.config;


import com.github.wxpay.sdk.WXPayConfig;
import com.github.wxpay.sdk.WXPayConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Component
public class WeixinPayConfig implements WXPayConfig {

    @Value("${payconfig.weixin.appId}")
    private String appId;
    @Value("${payconfig.weixin.mchID}")
    private String mchID;
    @Value("${payconfig.weixin.key}")
    private String key;
    @Value("${payconfig.weixin.notifyUrl}")
    private String notifyUrl;
    @Value("${payconfig.weixin.certPath}")
    private String certPath;
    private byte[] certData;

    @Override
    public String getAppID() {
        return this.appId;
    }

    @Override
    public String getMchID() {
        return this.mchID;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    /***
     * Get merchant certificate content
     * @return
     */
    @Override
    public InputStream getCertStream() {
        /****
         * Load certificate
         */
        if(certData==null){
            synchronized (WeixinPayConfig.class){
                try {
                    if(certData==null) {
                        File file = new File(certPath);
                        InputStream certStream = new FileInputStream(file);
                        this.certData = new byte[(int) file.length()];
                        certStream.read(this.certData);
                        certStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        ByteArrayInputStream certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return 0;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return 0;
    }
}
