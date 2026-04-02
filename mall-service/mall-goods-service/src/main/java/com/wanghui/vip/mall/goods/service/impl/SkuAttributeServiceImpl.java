package com.wanghui.vip.mall.goods.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanghui.vip.mall.goods.mapper.SkuAttributeMapper;
import com.wanghui.vip.mall.goods.model.SkuAttribute;
import com.wanghui.vip.mall.goods.service.SkuAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkuAttributeServiceImpl extends ServiceImpl<SkuAttributeMapper,SkuAttribute> implements SkuAttributeService {

    @Autowired
    private SkuAttributeMapper skuAttributeMapper;

    /*****
     * Query attribute list by category ID
     * @param id
     * @return
     */
    @Override
    public List<SkuAttribute> queryList(Integer id) {
        return skuAttributeMapper.queryByCategoryId(id);
    }
}
