package com.wanghui.vip.mall.pay.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanghui.vip.mall.pay.mapper.PayLogMapper;
import com.wanghui.vip.mall.pay.model.PayLog;
import com.wanghui.vip.mall.pay.service.PayLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayLogServiceImpl extends ServiceImpl<PayLogMapper,PayLog> implements PayLogService {

    @Autowired
    private PayLogMapper payLogMapper;

    /***
     * Add log
     * @param payLog
     */
    @Override
    public void add(PayLog payLog) {
        payLogMapper.deleteById(payLog.getId());
        payLogMapper.insert(payLog);
    }
}
