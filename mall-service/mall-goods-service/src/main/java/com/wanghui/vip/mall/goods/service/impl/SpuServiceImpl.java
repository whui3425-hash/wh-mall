package com.wanghui.vip.mall.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanghui.vip.mall.goods.mapper.BrandMapper;
import com.wanghui.vip.mall.goods.mapper.CategoryMapper;
import com.wanghui.vip.mall.goods.mapper.SkuMapper;
import com.wanghui.vip.mall.goods.mapper.SpuMapper;
import com.wanghui.vip.mall.goods.model.*;
import com.wanghui.vip.mall.goods.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl extends ServiceImpl<SpuMapper,Spu> implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    /***
     * Save product
     * @param product
     */
    @Override
    public void saveProduct(Product product) {
        //1. Save Spu
        Spu spu = product.getSpu();

        if(StringUtils.isEmpty(spu.getId())){
            spu.setIsMarketable(1); //On shelf
            spu.setIsDelete(0); //Not deleted
            spu.setStatus(1);   //Approved
            spuMapper.insert(spu);
        }else{
            spuMapper.updateById(spu);
            skuMapper.delete(new QueryWrapper<Sku>().eq("spu_id",spu.getId()));
        }

        //2. Save List<Sku>
        Date date = new Date();
        Category category = categoryMapper.selectById(spu.getCategoryThreeId());
        Brand brand = brandMapper.selectById(spu.getBrandId());
        for (Sku sku : product.getSkus()) {
            //SKU name
            String name = spu.getName();
            Map<String,String> skuattrMap = JSON.parseObject(sku.getSkuAttribute(),Map.class);
            for (Map.Entry<String, String> entry : skuattrMap.entrySet()) {
                name+="  "+entry.getValue();
            }
            sku.setName(name);
            sku.setCreateTime(date);
            sku.setUpdateTime(date);
            sku.setCategoryId(spu.getCategoryThreeId());
            sku.setBrandName(brand.getName());
            sku.setBrandId(spu.getBrandId());
            sku.setCategoryName(category.getName());
            sku.setSpuId(spu.getId());
            //Status: 1-normal, 2-off shelf, 3-deleted
            sku.setStatus(1);

            skuMapper.insert(sku);
        }
    }

    /****
     * Query Product by spu id
     * @param id
     * @return
     */
    @Override
    public Product findBySupId(String id) {
        Spu spu = spuMapper.selectById(id);
        QueryWrapper<Sku> queryWrapper = new QueryWrapper<Sku>();
        queryWrapper.eq("spu_id",id);
        List<Sku> skus = skuMapper.selectList(queryWrapper);

        Product product = new Product();
        product.setSpu(spu);
        product.setSkus(skus);
        return product;
    }
}
