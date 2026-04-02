package com.wanghui.vip.mall.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gupaoedu.mall.util.RespResult;
import com.wanghui.vip.mall.cart.mapper.CartMapper;
import com.wanghui.vip.mall.cart.model.Cart;
import com.wanghui.vip.mall.cart.service.CartService;
import com.wanghui.vip.mall.goods.feign.SkuFeign;
import com.wanghui.vip.mall.goods.model.Sku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private SkuFeign skuFeign;

    /***
     * Delete shopping cart list by IDs
     */
    @Override
    public void delete(List<String> ids) {
        // TODO: MongoDB degraded to MySQL, needs optimization for batch delete
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
            // TODO: MongoDB degraded to MySQL, needs optimization for batch query
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
        // Use MyBatis-Plus conditional query
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName);
        return cartMapper.selectList(queryWrapper);
    }

    /***
     * Add to shopping cart
     * @param id
     * @param userName
     * @param num: total quantity of current product in shopping cart
     * @return
     */
    @Override
    public void add(String id, String userName, Integer num) {
        // Generate composite primary key: username + SKU_ID
        String cartKey = userName + id;
        
        // 1) Check if shopping cart record exists
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName).eq("sku_id", id);
        Cart existingCart = cartMapper.selectOne(queryWrapper);
        
        if (existingCart != null) {
            // Delete old record if exists
            cartMapper.deleteById(existingCart.getId());
        }

        if (num > 0) {
            // 2) Query Sku details by ID
            RespResult<Sku> skuResp = skuFeign.one(id);

            // 3) Add current product to shopping cart (store in MySQL)
            Sku sku = skuResp.getData();
            Cart cart = new Cart();
            cart.setUserName(userName);
            cart.setName(sku.getName());
            cart.setPrice(sku.getPrice());
            cart.setImage(sku.getImage());
            cart.setSkuId(id);
            cart.setNum(num);
            // TODO: Get tenantId from login context
            cart.setTenantId("default");
            cartMapper.insert(cart);
        }
    }
}
