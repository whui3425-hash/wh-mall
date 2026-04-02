package com.wanghui.vip.mall.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanghui.vip.mall.goods.model.Product;
import com.wanghui.vip.mall.goods.model.Spu;

public interface SpuService extends IService<Spu> {

    /****
     * Save product
     */
    void saveProduct(Product product);

    Product findBySupId(String id);
}
