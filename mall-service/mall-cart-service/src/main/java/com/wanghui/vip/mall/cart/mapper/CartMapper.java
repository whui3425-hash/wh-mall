package com.wanghui.vip.mall.cart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wanghui.vip.mall.cart.model.Cart;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 购物车数据访问层
 */
public interface CartMapper extends BaseMapper<Cart> {

    /**
     * 根据用户名和SKU ID查询购物车记录
     * @param userName 用户名
     * @param skuId SKU ID
     * @return 购物车记录
     */
    @Select("SELECT * FROM mall_cart WHERE user_name = #{userName} AND sku_id = #{skuId} LIMIT 1")
    Cart selectByUserNameAndSkuId(@Param("userName") String userName, @Param("skuId") String skuId);

    /**
     * 根据用户名查询购物车列表（用于不带租户插件的场景）
     * @param userName 用户名
     * @return 购物车列表
     */
    @Select("SELECT * FROM mall_cart WHERE user_name = #{userName}")
    java.util.List<Cart> selectListByUserName(@Param("userName") String userName);
}
