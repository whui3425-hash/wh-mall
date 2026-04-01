package com.gupaoedu.vip.mall.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gupaoedu.vip.mall.cart.model.Cart;
import com.gupaoedu.vip.mall.goods.model.Sku;

import java.util.List;

public interface SKuService extends IService<Sku> {

    void dcount(List<Cart> carts);

    List<Sku> typeSkuItems(Integer id);

    void delTypeSkuItems(Integer id);

    List<Sku> updateTypeSkuItems(Integer id);
}
