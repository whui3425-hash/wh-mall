package com.wanghui.vip.mall.cart.service;

import com.wanghui.vip.mall.cart.model.Cart;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {

    /**
     * 【核心】添加商品到购物车
     * 根据skuId查询商品信息，判断购物车是否已存在该商品，存在则累加数量，不存在则新增
     * @param userId 用户ID（从JWT Header获取）
     * @param userName 用户名（从JWT Header获取）
     * @param skuId SKU ID
     * @param num 添加数量
     */
    void addCartItem(String userId, String userName, String skuId, Integer num);

    /**
     * 【核心】根据用户ID查询购物车列表（支持多租户隔离）
     * @param userId 用户ID（从JWT Header获取）
     * @param userName 用户名（从JWT Header获取，可为空）
     * @return 购物车列表
     */
    List<Cart> listByUserId(String userId, String userName);

    /**
     * 查询指定用户的指定SKU购物车记录
     * @param userId 用户ID
     * @param skuId SKU ID
     * @return 购物车记录，不存在返回null
     */
    Cart findByUserIdAndSkuId(String userId, String skuId);

    /**
     * 【核心】根据ID删除购物车记录（带用户权限验证）
     * @param id 购物车记录ID
     * @param userId 当前登录用户ID（从JWT Header获取，用于验证归属）
     * @return 删除成功返回true，失败返回false
     */
    boolean deleteById(Long id, String userId);

    /**
     * 【核心】修改购物车商品数量（带用户权限验证）
     * @param id 购物车记录ID
     * @param userId 当前登录用户ID（从JWT Header获取，用于验证归属）
     * @param num 新的数量
     * @return 更新成功返回true，失败返回false
     */
    boolean updateNum(Long id, String userId, Integer num);

    /**
     * 【核心】批量删除购物车记录（带用户权限验证）
     * @param ids 购物车记录ID列表
     * @param userId 当前登录用户ID（从JWT Header获取，用于验证归属）
     */
    void deleteBatch(List<Long> ids, String userId);

    /***
     * Delete shopping cart list by IDs (legacy)
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
     * Add to shopping cart (legacy)
     */
    void add(String id, String userName, Integer num);
}
