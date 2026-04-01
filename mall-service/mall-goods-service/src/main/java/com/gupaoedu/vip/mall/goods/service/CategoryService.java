package com.gupaoedu.vip.mall.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gupaoedu.vip.mall.goods.model.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    /***
     * Query child categories by parent ID
     */
    List<Category> findByParentId(Integer pid);
}
