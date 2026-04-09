package com.wanghui.vip.mall.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanghui.vip.mall.cart.model.Cart;
import com.wanghui.vip.mall.goods.model.Sku;

import java.util.List;

public interface SKuService extends IService<Sku> {

    void dcount(List<Cart> carts);

    List<Sku> typeSkuItems(Integer id);

    void delTypeSkuItems(Integer id);

    List<Sku> updateTypeSkuItems(Integer id);

    /**
     * 根据SPU ID获取第一个SKU
     * @param spuId SPU ID
     * @return 第一个SKU
     */
    Sku getFirstBySpuId(String spuId);
}
