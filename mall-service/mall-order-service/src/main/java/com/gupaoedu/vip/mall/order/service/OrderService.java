package com.gupaoedu.vip.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gupaoedu.vip.mall.order.model.Order;
import com.gupaoedu.vip.mall.order.model.OrderRefund;

public interface OrderService extends IService<Order> {

    /***
     * Refund
     */
    int refund(OrderRefund orderRefund);

    /***
     * Add order
     */
    Boolean add(Order order);

    /***
     * Update status after payment
     */
    int updateAfterPayStatus(String id);
}
