package com.wanghui.vip.mall.cart.feign;

import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.cart.model.Cart;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "mall-cart")
public interface CartFeign {

    @DeleteMapping(value = "/api/cart")
    RespResult delete(@RequestBody List<String> ids);

    @PostMapping(value = "/api/cart/list")
    RespResult<List<Cart>> list(@RequestBody List<String> ids);

    /**
     * 【订单提交】根据购物车商品ID列表查询商品详情
     * 用于订单提交时获取商品信息
     * @param cartItemIds 购物车商品ID列表
     * @return 购物车商品列表
     */
    @PostMapping(value = "/api/cart/listByIds")
    RespResult<List<Cart>> listByIds(@RequestBody List<Long> cartItemIds);

    /**
     * 【订单提交】根据购物车商品ID列表删除购物车商品
     * 订单支付成功后删除已购买的商品
     * @param cartItemIds 购物车商品ID列表
     * @return 删除结果
     */
    @DeleteMapping(value = "/api/cart/byIds")
    RespResult deleteByIds(@RequestBody List<Long> cartItemIds);
}
