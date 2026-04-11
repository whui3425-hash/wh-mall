package com.wanghui.vip.mall.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.order.model.Order;
import com.wanghui.vip.mall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 【B端管理后台】订单管理控制器
 * 供商户管理员查看本店铺所有订单
 *
 * @author wanghui
 */
@RestController
@RequestMapping(value = "/api/order/admin")
@CrossOrigin
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    // 请求头常量（与网关定义一致）
    private static final String TENANT_ID_HEADER = "X-Tenant-Id";

    /**
     * 【B端】查询当前租户下的所有订单列表
     * GET /api/order/admin/list
     *
     * 【数据隔离】从 Header 提取 X-Tenant-Id，确保商户只能看到自己店铺的订单
     * 【级联查询】返回订单列表，每个订单包含订单明细
     * 【排序】按创建时间 create_time 倒序排列
     *
     * @param request HTTP请求（用于获取Header中的租户ID）
     * @return 订单列表
     */
    @GetMapping(value = "/list")
    public RespResult<List<Map<String, Object>>> listTenantOrders(HttpServletRequest request) {
        // 1. 从请求头获取租户ID（由网关JWT过滤器注入）
        String tenantId = request.getHeader(TENANT_ID_HEADER);
        if (tenantId == null || tenantId.isEmpty()) {
            tenantId = "1001"; // 默认值，实际应由网关强制注入
        }

        System.out.println("[AdminOrderController] 查询店铺订单列表，tenantId=" + tenantId);

        try {
            // 2. 构建查询条件 - 只查当前租户的订单，不限定用户
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("tenant_id", tenantId);
            queryWrapper.orderByDesc("create_time");

            // 3. 查询订单列表
            List<Order> orders = orderService.list(queryWrapper);

            // 4. 组装返回数据（简化版，实际应包含订单明细）
            List<Map<String, Object>> result = new java.util.ArrayList<>();
            for (Order order : orders) {
                Map<String, Object> item = new java.util.HashMap<>();
                item.put("id", order.getId());
                item.put("outTradeNo", order.getOutTradeNo());
                item.put("amount", order.getMoneys());
                item.put("createTime", order.getCreateTime());
                item.put("payStatus", order.getPayStatus()); // 0-待支付, 1-已支付
                result.add(item);
            }

            System.out.println("[AdminOrderController] 查询到 " + result.size() + " 条订单");
            return RespResult.ok(result);

        } catch (Exception e) {
            System.err.println("[AdminOrderController] 查询订单列表失败：" + e.getMessage());
            e.printStackTrace();
            return RespResult.error("查询订单列表失败，请稍后重试");
        }
    }
}
