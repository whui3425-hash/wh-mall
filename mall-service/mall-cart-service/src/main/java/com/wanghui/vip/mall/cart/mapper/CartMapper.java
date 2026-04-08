package com.wanghui.vip.mall.cart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wanghui.vip.mall.cart.model.Cart;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 购物车数据访问层
 * 支持多租户和用户隔离
 */
public interface CartMapper extends BaseMapper<Cart> {

    /**
     * 【核心】根据用户ID和SKU ID查询购物车记录
     * 用于判断购物车中是否已存在该商品
     * @param userId 用户ID
     * @param skuId SKU ID
     * @return 购物车记录
     */
    @Select("SELECT * FROM mall_cart WHERE user_id = #{userId} AND sku_id = #{skuId} LIMIT 1")
    Cart selectByUserIdAndSkuId(@Param("userId") String userId, @Param("skuId") String skuId);

    /**
     * 【核心】根据用户ID查询购物车列表
     * @param userId 用户ID
     * @return 购物车列表
     */
    @Select("SELECT * FROM mall_cart WHERE user_id = #{userId} ORDER BY id DESC")
    List<Cart> selectListByUserId(@Param("userId") String userId);

    /**
     * 根据用户名和SKU ID查询购物车记录（旧版兼容）
     * @param userName 用户名
     * @param skuId SKU ID
     * @return 购物车记录
     */
    @Select("SELECT * FROM mall_cart WHERE user_name = #{userName} AND sku_id = #{skuId} LIMIT 1")
    Cart selectByUserNameAndSkuId(@Param("userName") String userName, @Param("skuId") String skuId);

    /**
     * 根据用户名查询购物车列表（旧版兼容）
     * @param userName 用户名
     * @return 购物车列表
     */
    @Select("SELECT * FROM mall_cart WHERE user_name = #{userName}")
    List<Cart> selectListByUserName(@Param("userName") String userName);
}
