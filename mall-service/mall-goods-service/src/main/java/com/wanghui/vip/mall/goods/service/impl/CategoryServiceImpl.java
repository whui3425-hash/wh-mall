package com.wanghui.vip.mall.goods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanghui.vip.mall.goods.mapper.CategoryMapper;
import com.wanghui.vip.mall.goods.model.Category;
import com.wanghui.vip.mall.goods.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /***
     * Query all child categories by parent ID
     */
    @Override
    public List<Category> findByParentId(Integer pid) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<Category>();
        queryWrapper.eq("parent_id",pid);
        return categoryMapper.selectList(queryWrapper);
    }
}
