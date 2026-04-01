package com.gupaoedu.vip.mall.pay.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.gupaoedu.mall.util.*;
import com.gupaoedu.vip.mall.pay.model.PayLog;
import com.gupaoedu.vip.mall.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/wx")
@CrossOrigin
public class WeixinPayController {

    @Autowired
    private WeixinPayService weixinPayService;

    @Autowired
    private Signature signature;

    //Secret key -> MD5 (skey)
    @Value("${payconfig.weixin.key}")
    private String skey;

    /****
     * Query order payment status
     */
    @GetMapping(value = "/result/{outno}")
    public RespResult<PayLog> result(@PathVariable(value = "outno")String outno) throws Exception{
        PayLog payLog = weixinPayService.result(outno);
        return RespResult.ok(payLog);
    }

    /****
     * Get WeChat payment QR code
     */
    @GetMapping(value = "/pay")
    public RespResult<Map> pay(@RequestParam("ciptext") String ciphertext) throws Exception {
        //ciphertext->AES->remove signature data signature->MD5==signature?
        Map<String, String> dataMap = signature.security(ciphertext);

        Map<String, String> map = weixinPayService.preOrder(dataMap);
        if(map!=null){
            map.put("orderNumber",dataMap.get("out_trade_no"));
            map.put("money",dataMap.get("total_fee"));
            return RespResult.ok(map);
        }
        return RespResult.error("Payment system is busy, please try again later!");
    }

    /****
     * Payment result callback
     */
    @RequestMapping(value = "/result")
    public String result(HttpServletRequest request) throws Exception{
        //Read network input stream
        ServletInputStream is = request.getInputStream();

        //Define output stream to receive input stream
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        //Read network input stream into output stream
        byte[] buffer = new byte[1024];
        int len=0;
        while ((len=is.read(buffer))!=-1){
            os.write(buffer,0,len);
        }

        //Close resources
        os.close();
        is.close();

        //Convert payment result XML to Map
        String xmlResult = new String(os.toByteArray(),"UTF-8");
        Map<String, String> map = WXPayUtil.xmlToMap(xmlResult);
        System.out.println("xmlResult:"+xmlResult);
        //Payment status: 2 success, 7 failure
        int status = 7;
        // return_code/result_code
        if(map.get("return_code").equals(WXPayConstants.SUCCESS) && map.get("result_code").equals(WXPayConstants.SUCCESS)){
            status=2;
        }

        //Create log object
        PayLog payLog = new PayLog(map.get("out_trade_no"),status,JSON.toJSONString(map),map.get("out_trade_no"),new Date());

        //Map response data
        Map<String,String> resultResp = new HashMap<String,String>();
        resultResp.put("return_code","SUCCESS");
        resultResp.put("return_msg","OK");
        return WXPayUtil.mapToXml(resultResp);
    }


    /****
     * Refund status
     */
    @RequestMapping(value = "/refund/result")
    public String refund(HttpServletRequest request) throws Exception{
        //Read network input stream
        ServletInputStream is = request.getInputStream();

        //Define output stream to receive input stream
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        //Read network input stream into output stream
        byte[] buffer = new byte[1024];
        int len=0;
        while ((len=is.read(buffer))!=-1){
            os.write(buffer,0,len);
        }

        //Close resources
        os.close();
        is.close();

        //Convert payment result XML to Map
        String xmlResult = new String(os.toByteArray(),"UTF-8");
        Map<String, String> map = WXPayUtil.xmlToMap(xmlResult);
        System.out.println("Refund data-xmlResult:"+xmlResult);

        //Get refund info (encrypted - AES)
        String reqinfo = map.get("req_info");
        String key = MD5.md5(skey);
        byte[] decode = AESUtil.encryptAndDecrypt(Base64Util.decode(reqinfo), key, 2);
        System.out.println("Decrypted refund data:"+new String(decode, "UTF-8"));

        //Map response data
        Map<String,String> resultResp = new HashMap<String,String>();
        resultResp.put("return_code","SUCCESS");
        resultResp.put("return_msg","OK");
        return WXPayUtil.mapToXml(resultResp);
    }
}
