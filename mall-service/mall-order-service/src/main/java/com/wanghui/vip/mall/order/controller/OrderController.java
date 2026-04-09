package com.wanghui.vip.mall.order.controller;

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
import java.util.Date;
import java.util.List;
import java.util.Map;

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

}
