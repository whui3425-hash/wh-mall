package com.wanghui.vip.mall.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanghui.vip.mall.pay.model.PayLog;

public interface PayLogService extends IService<PayLog> {

    void add(PayLog payLog);
}
