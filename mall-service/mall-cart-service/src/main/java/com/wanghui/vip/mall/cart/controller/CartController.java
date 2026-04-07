package com.wanghui.vip.mall.cart.controller;

import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.cart.model.Cart;
import com.wanghui.vip.mall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器 - C端买家购物车核心功能
 */
@RestController
@RequestMapping(value = "/api/cart")
@CrossOrigin
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加商品到购物车
     * @param addRequest 添加请求（skuId + num）
     * @return RespResult 操作结果
     */
    @PostMapping(value = "/add")
    public RespResult add(@RequestBody CartAddRequest addRequest) {
        // 参数校验
        if (addRequest == null || addRequest.getSkuId() == null || addRequest.getNum() == null) {
            return RespResult.error("参数不完整，需要skuId和num");
        }
        if (addRequest.getNum() <= 0) {
            return RespResult.error("添加数量必须大于0");
        }

        // 获取当前登录用户ID（临时写死，后续从JWT解析）
        String userId = "1";
        String userName = "zhangsan";

        cartService.addCartItem(userId, userName, addRequest.getSkuId(), addRequest.getNum());
        return RespResult.ok();
    }

    /**
     * 获取当前用户的购物车列表
     * @return RespResult<List<Cart>> 购物车商品列表
     */
    @GetMapping(value = "/list")
    public RespResult<List<Cart>> list() {
        // 获取当前登录用户ID（临时写死，后续从JWT解析）
        String userId = "1";

        List<Cart> list = cartService.listByUserId(userId);
        return RespResult.ok(list);
    }

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

    // ================== 原有接口（保留兼容）==================

    /***
     * Delete shopping cart data
     */
    @DeleteMapping
    public RespResult delete(@RequestBody List<String> ids){
        cartService.delete(ids);
        return RespResult.ok();
    }

    /***
     * Shopping cart data by specified ID collection
     */
    @PostMapping(value = "/list/ids")
    public RespResult<List<Cart>> listByIds(@RequestBody List<String> ids){
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
