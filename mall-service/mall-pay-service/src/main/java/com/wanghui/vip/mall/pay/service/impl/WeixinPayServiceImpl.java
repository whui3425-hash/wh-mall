package com.wanghui.vip.mall.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.wxpay.sdk.WXPay;
import com.wanghui.vip.mall.pay.config.WeixinPayConfig;
import com.wanghui.vip.mall.pay.mapper.PayLogMapper;
import com.wanghui.vip.mall.pay.model.PayLog;
import com.wanghui.vip.mall.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Autowired
    private WXPay wxPay;

    @Autowired
    private PayLogMapper payLogMapper;

    /****
     * Apply for refund
     * @param dataMap
     * @return
     */
    @Override
    public Map<String, String> refund(Map<String, String> dataMap) throws Exception {
        return wxPay.refund(dataMap);
    }

    /****
     * Pre-payment order operation (get payment QR code address)
     * @param dataMap
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, String> preOrder(Map<String, String> dataMap) throws Exception {
        return wxPay.unifiedOrder(dataMap);
    }


    /****
     * Query payment result
     * @param outno
     * @return
     * @throws Exception
     */
    @Override
    public PayLog result(String outno) throws Exception {
        // Query payment log from database
        PayLog payLog = payLogMapper.selectById(outno);

        if(payLog==null){
            // Query WeChat payment service if no data in database
            Map<String, String> data = new HashMap<String, String>();
            data.put("out_trade_no",outno);
            Map<String, String> resp = wxPay.orderQuery(data);
            // Save payment result to database (irreversible payment result)
            // return_code result_code trade_state
            String tradeState = resp.get("trade_state");
            int status = tradeState(tradeState);

            // Record log for irreversible payment status
            if(status==2 || status==3 || status==4 || status==5 || status==7){
                payLog = new PayLog(outno,status, JSON.toJSONString(resp),outno,new Date());
                payLogMapper.insert(payLog);
            }
        }
        return payLog;
    }

    /***
     * Payment status
     * @param tradeState
     * @return
     */
    public int tradeState(String tradeState){
        int state = 1;
        switch (tradeState){
            case "NOTPAY":  // Not paid
                state = 1;
                break;
            case "SUCCESS":
                state = 2;  // Paid
                break;
            case "REFUND":
                state = 3;  // Refunded
                break;
            case "CLOSED":
                state = 4;  // Closed
                break;
            case "REVOKED":
                state = 5;  // Revoked
                break;
            case "USERPAYING":
                state = 6;  // User paying
                break;
            case "PAYERROR":
                state = 7;  // Payment failed
                break;
            default:
                state=1;
        }
        return state;
    }
}
