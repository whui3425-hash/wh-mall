package com.wanghui.vip.mall.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.wanghui.mall.util.RespCode;
import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.order.model.Order;
import com.wanghui.vip.mall.order.model.OrderRefund;
import com.wanghui.vip.mall.order.service.OrderService;
import com.wanghui.vip.mall.pay.WeixinPayParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping(value = "/api/order")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WeixinPayParam weixinPayParam;

    // 请求头常量（与网关定义一致）
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_NAME_HEADER = "X-User-Name";
    private static final String TENANT_ID_HEADER = "X-Tenant-Id";

    // TODO: [架构师注] 当前为单机进程锁，用于模拟 Redis 分布式锁防重。后续部署多节点集群时，必须替换为真实的 Redis Redisson 分布式锁！
    /**
     * 【本地 Redis 模拟】使用 ConcurrentHashMap 模拟 Redis 的 SETNX 分布式锁
     * Key: pay_lock:{outTradeNo} | Value: Boolean.TRUE
     */
    private static final ConcurrentHashMap<String, Boolean> LOCAL_REDIS_MOCK = new ConcurrentHashMap<>();

    /****
     * Apply for order cancellation (simulate refund order for testing)
     */
    @PutMapping(value = "/refund/{id}")
    public RespResult refund(@PathVariable(value = "id")String id,HttpServletRequest request) throws Exception{
        //Username
        String username = "gp";
        //Query order to check if it meets refund requirements
        Order order = orderService.getById(id);
        if(order.getPayStatus().intValue()==1 && order.getOrderStatus().intValue()==1){
            //Add refund record, update order status
            OrderRefund orderRefund = new OrderRefund(
                    IdWorker.getIdStr(),
                    id,
                    1,
                    null,
                    username,
                    0,//Apply for refund
                    new Date(),
                    order.getMoneys()
            );
            orderService.refund(orderRefund);

            //Simplified version returns success directly, should call payment service Feign interface in practice
            return RespResult.ok();
        }
        //Return error if conditions not met
        return RespResult.error("Current order does not meet cancellation requirements!");
    }


    /***
     * Add order (legacy method)
     */
    @PostMapping(value = "/add")
    public RespResult add(@RequestBody Order order, HttpServletRequest request) throws Exception {
        //Username
        order.setUsername("gp");
        //Place order
        Boolean bo = orderService.add(order);
        String ciptxt = weixinPayParam.weixinParam(order, request);
        return bo? RespResult.ok(ciptxt) : RespResult.error(RespCode.ERROR);
    }

    /**
     * 【核心】提交订单（分布式防超卖版）
     * POST /api/order/submit
     *
     * 流程：
     * 1. 接收购物车商品ID列表（cartItemIds）
     * 2. 从请求头获取 userId、username、tenantId（由网关JWT过滤器注入）
     * 3. 调用 OrderService.submitOrder 执行：
     *    - 原子性扣减库存（防超卖）
     *    - 创建订单（状态UNPAID）
     *    - 删除购物车商品
     * 4. 返回 outTradeNo（支付流水号）和 totalAmount（总金额），供前端拉起支付
     *
     * @param submitRequest 包含 cartItemIds（购物车商品ID列表）
     * @param request HTTP请求（用于获取Header中的用户信息）
     * @return 包含 outTradeNo、totalAmount、orderId、status 的JSON
     */
    @PostMapping(value = "/submit")
    public RespResult<Map<String, Object>> submitOrder(@RequestBody SubmitOrderRequest submitRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (submitRequest == null || submitRequest.getCartItemIds() == null || submitRequest.getCartItemIds().isEmpty()) {
            return RespResult.error("购物车商品ID列表不能为空");
        }

        // 2. 从请求头获取用户信息（由网关JWT过滤器注入）
        String userId = getHeaderValue(request, USER_ID_HEADER, "1");
        String username = getHeaderValue(request, USER_NAME_HEADER, "zhangsan");
        String tenantId = getHeaderValue(request, TENANT_ID_HEADER, "1001");

        System.out.println("[OrderController] 提交订单请求，userId=" + userId + ", tenantId=" + tenantId);

        try {
            // 3. 调用Service层执行订单提交（含事务）
            Map<String, Object> result = orderService.submitOrder(
                    submitRequest.getCartItemIds(),
                    userId,
                    username,
                    tenantId
            );

            // 4. 返回成功结果（包含 outTradeNo 和 totalAmount）
            return RespResult.ok(result);

        } catch (RuntimeException e) {
            // 库存不足等业务异常
            System.err.println("[OrderController] 订单提交失败：" + e.getMessage());
            return RespResult.error(e.getMessage());
        } catch (Exception e) {
            // 系统异常
            System.err.println("[OrderController] 订单提交异常：" + e.getMessage());
            e.printStackTrace();
            return RespResult.error("订单提交失败，请稍后重试");
        }
    }

    /**
     * 【C端】查询买家订单列表（带订单明细）
     * GET /api/order/list
     *
     * 【数据隔离】从 Header 提取 X-User-Id 和 X-Tenant-Id，确保买家只能看到自己的订单
     * 【级联查询】返回订单列表，每个订单包含 OrderSku 明细（买了哪些商品）
     * 【排序】按创建时间 create_time 倒序排列
     *
     * @param request HTTP请求（用于获取Header中的用户ID和租户ID）
     * @return 订单列表，包含订单基础信息和明细
     */
    @GetMapping(value = "/list")
    public RespResult<List<Map<String, Object>>> listBuyerOrders(HttpServletRequest request) {
        // 1. 从请求头获取用户信息（由网关JWT过滤器注入）
        String userId = getHeaderValue(request, USER_ID_HEADER, "1");
        String tenantId = getHeaderValue(request, TENANT_ID_HEADER, "1001");

        System.out.println("[OrderController] 查询买家订单列表，userId=" + userId + ", tenantId=" + tenantId);

        try {
            // 2. 调用Service层查询订单列表（带明细）
            List<Map<String, Object>> orders = orderService.listBuyerOrdersWithDetails(userId, tenantId);

            // 3. 返回订单列表
            return RespResult.ok(orders);

        } catch (Exception e) {
            System.err.println("[OrderController] 查询订单列表失败：" + e.getMessage());
            e.printStackTrace();
            return RespResult.error("查询订单列表失败，请稍后重试");
        }
    }

    /**
     * 获取请求头值（带默认值）
     */
    private String getHeaderValue(HttpServletRequest request, String headerName, String defaultValue) {
        String value = request.getHeader(headerName);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    /**
     * 提交订单请求DTO（内部类）
     */
    public static class SubmitOrderRequest {
        private List<Long> cartItemIds;  // 购物车商品ID列表

        public List<Long> getCartItemIds() {
            return cartItemIds;
        }

        public void setCartItemIds(List<Long> cartItemIds) {
            this.cartItemIds = cartItemIds;
        }
    }

    /**
     * 支付回调请求DTO（内部类）
     */
    public static class PayCallbackRequest {
        private String outTradeNo;  // 外部交易流水号

        public String getOutTradeNo() {
            return outTradeNo;
        }

        public void setOutTradeNo(String outTradeNo) {
            this.outTradeNo = outTradeNo;
        }
    }

    /**
     * 【核心】支付回调接口（防重复回调版）
     * POST /api/order/pay/callback
     *
     * 【分布式锁模拟】使用本地 ConcurrentHashMap 模拟 Redis SETNX，防止高并发重复回调
     * 流程：
     * 1. 接收支付平台回调，获取 outTradeNo
     * 2. 【获取锁】使用 putIfAbsent 模拟 SETNX，成功获取锁则继续处理
     * 3. 【幂等校验】查询订单状态，已支付则直接返回（防重复处理）
     * 4. 【业务处理】更新订单为已支付状态
     * 5. 【释放锁】在 finally 块中移除锁标记
     *
     * @param callbackRequest 包含 outTradeNo（支付流水号）
     * @return 处理结果
     */
    @PostMapping(value = "/pay/callback")
    public RespResult payCallback(@RequestBody PayCallbackRequest callbackRequest) {
        // 1. 参数校验
        if (callbackRequest == null || callbackRequest.getOutTradeNo() == null || callbackRequest.getOutTradeNo().isEmpty()) {
            return RespResult.error("支付流水号不能为空");
        }

        String outTradeNo = callbackRequest.getOutTradeNo();
        String lockKey = "pay_lock:" + outTradeNo;

        // TODO: [架构师注] 当前为单机进程锁，用于模拟 Redis 分布式锁防重。后续部署多节点集群时，必须替换为真实的 Redis Redisson 分布式锁！
        // 2. 【获取分布式锁】使用 putIfAbsent 模拟 Redis SETNX
        Boolean alreadyLocked = LOCAL_REDIS_MOCK.putIfAbsent(lockKey, Boolean.TRUE);

        // 如果锁已存在（alreadyLocked != null），说明正在处理或已处理，直接返回
        if (alreadyLocked != null) {
            System.out.println("[PayCallback] 重复回调或正在处理中，忽略。outTradeNo=" + outTradeNo);
            return RespResult.ok("处理中，忽略重复回调");
        }

        // 成功获取锁，继续处理
        System.out.println("[PayCallback] 获取锁成功，开始处理回调。outTradeNo=" + outTradeNo + ", lockKey=" + lockKey);

        try {
            // 3. 【幂等性校验】查询订单当前状态
            // 根据 out_trade_no 查询订单（实际应该用索引查询）
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("out_trade_no", outTradeNo);
            Order order = orderService.getOne(queryWrapper);

            if (order == null) {
                System.out.println("[PayCallback] 订单不存在，outTradeNo=" + outTradeNo);
                return RespResult.error("订单不存在");
            }

            // 如果订单已经是已支付状态，直接返回（幂等）
            if (order.getPayStatus() != null && order.getPayStatus().intValue() == 1) {
                System.out.println("[PayCallback] 订单已支付，忽略重复回调。orderId=" + order.getId());
                return RespResult.ok("订单已支付，忽略重复回调");
            }

            // 4. 【业务处理】更新订单为已支付状态
            System.out.println("[PayCallback] 更新订单为已支付状态。orderId=" + order.getId());
            int updated = orderService.updateAfterPayStatus(order.getId());

            if (updated > 0) {
                System.out.println("[PayCallback] 订单支付状态更新成功。orderId=" + order.getId());
                return RespResult.ok("支付成功，订单状态已更新");
            } else {
                System.out.println("[PayCallback] 订单状态更新失败（可能已被其他线程更新）。orderId=" + order.getId());
                return RespResult.ok("订单状态已处理");
            }

        } catch (Exception e) {
            System.err.println("[PayCallback] 处理异常，outTradeNo=" + outTradeNo + ", 异常：" + e.getMessage());
            e.printStackTrace();
            return RespResult.error("处理异常：" + e.getMessage());
        } finally {
            // 5. 【释放锁】必须执行，防止死锁
            LOCAL_REDIS_MOCK.remove(lockKey);
            System.out.println("[PayCallback] 锁已释放。lockKey=" + lockKey);
        }
    }

}
