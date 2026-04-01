package com.gupaoedu.vip.mall.goods.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gupaoedu.vip.mall.goods.model.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface SkuMapper extends BaseMapper<Sku> {

    /***
     * Inventory decrease
     */
    @Update("update sku set num=num-#{num} where id=#{id} and num>=#{num}")
    int dcount(@Param("id")String id,@Param("num")Integer num);
}
