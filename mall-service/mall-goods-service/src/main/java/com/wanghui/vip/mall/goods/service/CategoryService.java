package com.wanghui.vip.mall.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanghui.vip.mall.goods.model.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    /***
     * Query child categories by parent ID
     */
    List<Category> findByParentId(Integer pid);
}
