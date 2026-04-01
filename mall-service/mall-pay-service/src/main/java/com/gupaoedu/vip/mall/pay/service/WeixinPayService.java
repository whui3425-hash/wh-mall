package com.gupaoedu.vip.mall.pay.service;

import com.gupaoedu.vip.mall.pay.model.PayLog;

import java.util.Map;

public interface WeixinPayService {

    /***
     * Refund application
     */
    Map<String,String> refund(Map<String,String> dataMap) throws Exception;

    // Pre-order creation method - get payment URL
    Map<String,String> preOrder(Map<String,String> dataMap) throws Exception;

    /****
     * Query payment result
     * outno: order number
     */
    PayLog result(String outno) throws Exception;
}
