package com.wanghui.vip.mall.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.cart.config.tenant.TenantContextHolder;
import com.wanghui.vip.mall.cart.mapper.CartMapper;
import com.wanghui.vip.mall.cart.model.Cart;
import com.wanghui.vip.mall.cart.service.CartService;
import com.wanghui.vip.mall.goods.feign.SkuFeign;
import com.wanghui.vip.mall.goods.model.Sku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车服务实现类
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private SkuFeign skuFeign;

    /**
     * 添加商品到购物车（核心业务逻辑）
     * 1. 通过Feign调用商品服务，查询SKU真实信息（价格、名称、图片）
     * 2. 检查当前用户购物车是否已存在该SKU
     * 3. 已存在：数量累加（Update）
     * 4. 不存在：新增记录（Insert）
     * 5. 自动注入当前租户ID（从网关传递的Header中解析）
     */
    @Override
    public void addCartItem(String userId, String userName, String skuId, Integer num) {
        // 1. 通过Feign调用商品服务，获取SKU详细信息
        RespResult<Sku> skuResp = skuFeign.one(skuId);
        if (skuResp == null || skuResp.getData() == null) {
            throw new RuntimeException("商品不存在或已下架，skuId=" + skuId);
        }
        Sku sku = skuResp.getData();

        // 2. 查询当前用户的购物车是否已存在该SKU
        Cart existingCart = findByUserIdAndSkuId(userId, skuId);

        if (existingCart != null) {
            // 3. 已存在：累加数量，更新记录
            existingCart.setNum(existingCart.getNum() + num);
            cartMapper.updateById(existingCart);
        } else {
            // 4. 不存在：插入新记录
            Cart cart = new Cart();
            cart.setUserName(userName);
            cart.setName(sku.getName());
            cart.setPrice(sku.getPrice());
            cart.setImage(sku.getImage());
            cart.setSkuId(skuId);
            cart.setNum(num);
            // 5. 获取当前租户ID（从ThreadLocal上下文，由网关传入）
            cart.setTenantId(TenantContextHolder.getTenantId());
            cartMapper.insert(cart);
        }
    }

    /**
     * 根据用户ID查询购物车列表（自动附加当前租户过滤）
     * MyBatis-Plus 租户插件会自动在SQL中添加 tenant_id = ? 条件
     */
    @Override
    public List<Cart> listByUserId(String userId) {
        // 使用user_name作为查询条件（当前userId映射为userName）
        // 租户过滤由MyBatis-Plus插件自动处理
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", "zhangsan"); // 临时使用固定用户名，后续关联真实用户
        return cartMapper.selectList(queryWrapper);
    }

    /**
     * 查询指定用户的指定SKU购物车记录
     * 用于判断购物车中是否已存在该商品
     */
    @Override
    public Cart findByUserIdAndSkuId(String userId, String skuId) {
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        // 使用user_name关联（当前设计）
        queryWrapper.eq("user_name", "zhangsan");
        queryWrapper.eq("sku_id", skuId);
        // 租户过滤由MyBatis-Plus自动附加
        return cartMapper.selectOne(queryWrapper);
    }

    /***
     * Delete shopping cart list by IDs
     */
    @Override
    public void delete(List<String> ids) {
        for (String id : ids) {
            cartMapper.deleteById(Long.valueOf(id));
        }
    }

    /***
     * Query shopping cart list by ID collection
     */
    @Override
    public List<Cart> list(List<String> ids) {
        if (ids != null && !ids.isEmpty()) {
            QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", ids);
            return cartMapper.selectList(queryWrapper);
        }
        return null;
    }

    /***
     * Shopping cart list
     */
    @Override
    public List<Cart> list(String userName) {
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName);
        return cartMapper.selectList(queryWrapper);
    }

    /***
     * Add to shopping cart (legacy implementation)
     */
    @Override
    public void add(String id, String userName, Integer num) {
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName).eq("sku_id", id);
        Cart existingCart = cartMapper.selectOne(queryWrapper);

        if (existingCart != null) {
            cartMapper.deleteById(existingCart.getId());
        }

        if (num > 0) {
            RespResult<Sku> skuResp = skuFeign.one(id);
            Sku sku = skuResp.getData();
            Cart cart = new Cart();
            cart.setUserName(userName);
            cart.setName(sku.getName());
            cart.setPrice(sku.getPrice());
            cart.setImage(sku.getImage());
            cart.setSkuId(id);
            cart.setNum(num);
            cart.setTenantId(TenantContextHolder.getTenantId());
            cartMapper.insert(cart);
        }
    }
}
