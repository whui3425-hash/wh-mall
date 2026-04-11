package com.wanghui.vip.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanghui.vip.mall.order.model.Order;
import com.wanghui.vip.mall.order.model.OrderRefund;

import java.util.List;
import java.util.Map;

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

    /**
     * 【核心】提交订单（分布式防超卖版）
     * 1. 扣减库存（调用goods-service，原子性扣减）
     * 2. 创建订单（状态UNPAID）
     * 3. 删除购物车商品
     * @param cartItemIds 购物车商品ID列表
     * @param userId 用户ID（从JWT Header获取）
     * @param username 用户名
     * @param tenantId 租户ID（多租户隔离）
     * @return 包含outTradeNo和totalAmount的Map
     */
    Map<String, Object> submitOrder(List<Long> cartItemIds, String userId, String username, String tenantId);

    /**
     * 【C端】查询买家订单列表（带订单明细）
     * 1. 根据 userId 和 tenantId 查询订单（多租户隔离）
     * 2. 级联查询每个订单的 OrderSku 明细
     * 3. 按创建时间倒序排列
     * @param userId 买家用户ID
     * @param tenantId 租户ID（店铺ID）
     * @return 订单列表，每个订单包含明细
     */
    List<Map<String, Object>> listBuyerOrdersWithDetails(String userId, String tenantId);
}
