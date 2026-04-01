package com.gupaoedu.vip.mall.goods.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gupaoedu.vip.mall.goods.model.SkuAttribute;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SkuAttributeMapper extends BaseMapper<SkuAttribute> {

    /****
     * Query attribute list by category ID
     */
    @Select("select * from sku_attribute where id IN(SELECT attr_id FROM category_attr WHERE category_id=#{id})")
    List<SkuAttribute> queryByCategoryId(Integer id);

}
