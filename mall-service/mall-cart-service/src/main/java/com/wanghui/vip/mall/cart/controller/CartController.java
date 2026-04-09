package com.wanghui.vip.mall.cart.controller;

import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.cart.model.Cart;
import com.wanghui.vip.mall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 购物车控制器 - C端买家购物车核心功能
 * 【关键】从 HttpServletRequest Header 中获取 X-User-Id 和 X-User-Name（网关注入）
 */
@RestController
@RequestMapping(value = "/api/cart")
@CrossOrigin
public class CartController {

    @Autowired
    private CartService cartService;

    // ================== Header 常量定义 ==================
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_NAME_HEADER = "X-User-Name";
    private static final String TENANT_ID_HEADER = "X-Tenant-Id";

    /**
     * 【核心】添加商品到购物车
     * POST /api/cart/add
     * @param addRequest 添加请求（skuId + num）
     * @param request HttpServletRequest（从中获取Header）
     * @return RespResult 操作结果
     */
    @PostMapping(value = "/add")
    public RespResult add(@RequestBody CartAddRequest addRequest, HttpServletRequest request) {
        // 参数校验
        if (addRequest == null || addRequest.getSkuId() == null || addRequest.getNum() == null) {
            return RespResult.error("参数不完整，需要skuId和num");
        }
        if (addRequest.getNum() <= 0) {
            return RespResult.error("添加数量必须大于0");
        }

        // 【关键】从 Header 获取当前登录用户信息（网关通过JWT解析后注入）
        String userId = getHeaderValue(request, USER_ID_HEADER, "1");
        String userName = getHeaderValue(request, USER_NAME_HEADER, "buyer");
        String tenantId = getHeaderValue(request, TENANT_ID_HEADER, "1001");

        System.out.println("[Cart] Add item - UserId: " + userId + ", UserName: " + userName + ", Tenant: " + tenantId);

        cartService.addCartItem(userId, userName, addRequest.getSkuId(), addRequest.getNum());
        return RespResult.ok();
    }

    /**
     * 【核心】获取当前用户的购物车列表
     * GET /api/cart/list
     * @param request HttpServletRequest（从中获取Header）
     * @return RespResult<List<Cart>> 购物车商品列表
     */
    @GetMapping(value = "/list")
    public RespResult<List<Cart>> list(HttpServletRequest request) {
        // 【关键】从 Header 获取当前登录用户ID
        String userId = getHeaderValue(request, USER_ID_HEADER, "1");
        String userName = getHeaderValue(request, USER_NAME_HEADER, null);

        System.out.println("[Cart] List items - UserId: " + userId + ", UserName: " + userName);

        List<Cart> list = cartService.listByUserId(userId, userName);
        return RespResult.ok(list);
    }

    /**
     * 【核心】删除购物车指定项
     * DELETE /api/cart/{id}
     * @param id 购物车记录ID
     * @param request HttpServletRequest（从中获取Header验证归属）
     * @return RespResult 操作结果
     */
    @DeleteMapping(value = "/{id}")
    public RespResult deleteById(@PathVariable("id") Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            return RespResult.error("无效的购物车ID");
        }

        // 【关键】从 Header 获取当前登录用户ID（用于验证归属权）
        String userId = getHeaderValue(request, USER_ID_HEADER, "1");

        System.out.println("[Cart] Delete item - ID: " + id + ", UserId: " + userId);

        boolean success = cartService.deleteById(id, userId);
        if (success) {
            return RespResult.ok();
        } else {
            return RespResult.error("删除失败，记录不存在或无权限");
        }
    }

    /**
     * 【核心】修改购物车商品数量
     * PUT /api/cart/update
     * @param updateRequest 修改请求（id + num）
     * @param request HttpServletRequest（从中获取Header验证归属）
     * @return RespResult 操作结果
     */
    @PutMapping(value = "/update")
    public RespResult update(@RequestBody CartUpdateRequest updateRequest, HttpServletRequest request) {
        // 参数校验
        if (updateRequest == null || updateRequest.getId() == null || updateRequest.getNum() == null) {
            return RespResult.error("参数不完整，需要id和num");
        }
        if (updateRequest.getNum() <= 0) {
            return RespResult.error("数量必须大于0");
        }

        // 【关键】从 Header 获取当前登录用户ID（用于验证归属权）
        String userId = getHeaderValue(request, USER_ID_HEADER, "1");

        System.out.println("[Cart] Update item - ID: " + updateRequest.getId() + ", Num: " + updateRequest.getNum() + ", UserId: " + userId);

        boolean success = cartService.updateNum(updateRequest.getId(), userId, updateRequest.getNum());
        if (success) {
            return RespResult.ok();
        } else {
            return RespResult.error("更新失败，记录不存在或无权限");
        }
    }

    /**
     * 【核心】批量删除购物车商品
     * DELETE /api/cart/batch
     * @param ids 购物车记录ID列表
     * @param request HttpServletRequest（从中获取Header验证归属）
     * @return RespResult 操作结果
     */
    @DeleteMapping(value = "/batch")
    public RespResult deleteBatch(@RequestBody List<Long> ids, HttpServletRequest request) {
        if (ids == null || ids.isEmpty()) {
            return RespResult.error("请选择要删除的商品");
        }

        // 【关键】从 Header 获取当前登录用户ID（用于验证归属权）
        String userId = getHeaderValue(request, USER_ID_HEADER, "1");

        System.out.println("[Cart] Batch delete - IDs: " + ids + ", UserId: " + userId);

        cartService.deleteBatch(ids, userId);
        return RespResult.ok();
    }

    // ================== 辅助方法 ==================

    /**
     * 从 Request Header 获取值，带默认值
     */
    private String getHeaderValue(HttpServletRequest request, String headerName, String defaultValue) {
        String value = request.getHeader(headerName);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    // ================== DTO 定义 ==================

    /**
     * 添加购物车请求DTO
     */
    public static class CartAddRequest {
        private String skuId;
        private Integer num;

        public String getSkuId() {
            return skuId;
        }

        public void setSkuId(String skuId) {
            this.skuId = skuId;
        }

        public Integer getNum() {
            return num;
        }

        public void setNum(Integer num) {
            this.num = num;
        }
    }

    /**
     * 更新购物车请求DTO
     */
    public static class CartUpdateRequest {
        private Long id;
        private Integer num;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Integer getNum() {
            return num;
        }

        public void setNum(Integer num) {
            this.num = num;
        }
    }

    /**
     * 【订单服务调用】根据购物车商品ID列表查询商品详情
     * POST /api/cart/listByIds
     * @param cartItemIds 购物车商品ID列表（Long类型）
     * @return 购物车商品列表
     */
    @PostMapping(value = "/listByIds")
    public RespResult<List<Cart>> listByIdsForOrder(@RequestBody List<Long> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return RespResult.ok(java.util.Collections.emptyList());
        }
        System.out.println("[Cart] 订单服务查询购物车，IDs: " + cartItemIds);
        List<Cart> carts = cartService.listByIds(cartItemIds);
        return RespResult.ok(carts != null ? carts : java.util.Collections.emptyList());
    }

    /**
     * 【订单服务调用】批量删除购物车商品（订单提交后清理）
     * DELETE /api/cart/byIds
     * @param cartItemIds 购物车商品ID列表
     * @return 删除结果
     */
    @DeleteMapping(value = "/byIds")
    public RespResult deleteByIdsForOrder(@RequestBody List<Long> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return RespResult.error("购物车ID列表不能为空");
        }
        System.out.println("[Cart] 订单服务删除购物车，IDs: " + cartItemIds);
        cartService.deleteBatch(cartItemIds, null);  // null 表示不校验用户权限（订单服务调用时已通过）
        return RespResult.ok();
    }

    // ================== 原有接口（保留兼容）==================

    /***
     * Delete shopping cart data (legacy batch delete)
     */
    @DeleteMapping
    public RespResult delete(@RequestBody List<String> ids){
        cartService.delete(ids);
        return RespResult.ok();
    }

    /***
     * Shopping cart data by specified ID collection (legacy, uses String IDs)
     */
    @PostMapping(value = "/list/ids")
    public RespResult<List<Cart>> listByIdsLegacy(@RequestBody List<String> ids){
        List<Cart> carts = cartService.list(ids);
        return RespResult.ok(carts);
    }

    /****
     * Add to shopping cart (legacy endpoint)
     */
    @GetMapping(value = "/{id}/{num}")
    public RespResult addLegacy(@PathVariable(value = "id")String id,
                          @PathVariable(value = "num")Integer num){
        String userName = "gp";
        cartService.add(id, userName, num);
        return RespResult.ok();
    }

    /****
     * Shopping cart list (legacy endpoint)
     */
    @GetMapping(value = "/list/legacy")
    public RespResult<List<Cart>> listLegacy(){
        String userName = "gp";
        List<Cart> list = cartService.list(userName);
        return RespResult.ok(list);
    }
}
