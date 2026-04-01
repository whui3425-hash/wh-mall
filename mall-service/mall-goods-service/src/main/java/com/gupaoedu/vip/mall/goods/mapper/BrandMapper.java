package com.gupaoedu.vip.mall.goods.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gupaoedu.vip.mall.goods.model.Brand;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BrandMapper extends BaseMapper<Brand> {
    /****
     * Query brand IDs by category ID
     */
    @Select("SELECT brand_id FROM category_brand WHERE category_id=#{id}")
    List<Integer> queryBrandIds(Integer id);
}
