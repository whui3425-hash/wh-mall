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

    /**
     * 【绝对物理防超卖】库存扣减
     * 使用数据库层面的乐观锁（num >= #{num}）防止并发超卖
     * @param skuId SKU ID
     * @param num 扣减数量
     * @throws RuntimeException 库存不足时抛出异常
     */
    void decrStock(String skuId, Integer num);
}
