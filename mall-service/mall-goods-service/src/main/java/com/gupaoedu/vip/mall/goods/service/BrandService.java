package com.gupaoedu.vip.mall.goods.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gupaoedu.vip.mall.goods.model.Brand;

import java.util.List;

public interface BrandService extends IService<Brand> {

    /****
     * Conditional query
     * return List<Brand>
     */
    List<Brand> queryList(Brand brand);

    /****
     * Conditional pagination query
     * return Page<Brand>
     */
    Page<Brand> queryPageList(Brand brand,Long currentPage,Long size);

    /***
     * Query brand collection by category ID
     */
    List<Brand> queryByCategoryId(Integer id);
}
