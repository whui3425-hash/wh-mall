package com.wanghui.vip.mall.cart.service;

import com.wanghui.vip.mall.cart.model.Cart;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {

    /**
     * 添加商品到购物车（核心方法）
     * 根据skuId查询商品信息，判断购物车是否已存在该商品，存在则累加数量，不存在则新增
     * @param userId 用户ID
     * @param userName 用户名
     * @param skuId SKU ID
     * @param num 添加数量
     */
    void addCartItem(String userId, String userName, String skuId, Integer num);

    /**
     * 根据用户ID查询购物车列表（支持多租户隔离）
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<Cart> listByUserId(String userId);

    /**
     * 查询指定用户的指定SKU购物车记录
     * @param userId 用户ID
     * @param skuId SKU ID
     * @return 购物车记录，不存在返回null
     */
    Cart findByUserIdAndSkuId(String userId, String skuId);

    /***
     * Delete shopping cart list by IDs
     */
    void delete(List<String> ids);

    /***
     * Query shopping cart list by ID collection
     */
    List<Cart> list(List<String> ids);

    /***
     * Shopping cart list
     */
    List<Cart> list(String userName);

    /***
     * Add to shopping cart
     */
    void add(String id, String userName, Integer num);
}
