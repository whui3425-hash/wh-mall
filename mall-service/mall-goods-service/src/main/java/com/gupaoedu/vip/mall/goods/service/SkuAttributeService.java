package com.gupaoedu.vip.mall.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gupaoedu.vip.mall.goods.model.SkuAttribute;

import java.util.List;

public interface SkuAttributeService extends IService<SkuAttribute> {


    /***
     * Query attribute list by category ID
     */
    List<SkuAttribute> queryList(Integer id);
}
