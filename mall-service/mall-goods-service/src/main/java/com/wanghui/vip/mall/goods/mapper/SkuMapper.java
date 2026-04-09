package com.wanghui.vip.mall.goods.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wanghui.vip.mall.goods.model.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface SkuMapper extends BaseMapper<Sku> {

    /***
     * Inventory decrease
     */
    @Update("update sku set num=num-#{num} where id=#{id} and num>=#{num}")
    int dcount(@Param("id")String id,@Param("num")Integer num);

    /**
     * 根据SPU ID查询SKU列表
     * @param spuId SPU ID
     * @return SKU列表
     */
    @Select("SELECT * FROM sku WHERE spu_id = #{spuId} AND status = 1 ORDER BY id LIMIT 1")
    List<Sku> selectBySpuId(@Param("spuId") String spuId);

    /**
     * 查询第一个可用的SKU（用于SPU默认添加购物车）
     * @param spuId SPU ID
     * @return 第一个SKU
     */
    @Select("SELECT * FROM sku WHERE spu_id = #{spuId} AND status = 1 ORDER BY id LIMIT 1")
    Sku selectFirstBySpuId(@Param("spuId") String spuId);

    /**
     * 【绝对物理防超卖】原子性库存扣减
     * 使用 UPDATE ... WHERE num >= #{num} 实现乐观锁，防止并发超卖
     * @param skuId SKU ID
     * @param num 扣减数量
     * @return 影响行数：1表示扣减成功，0表示库存不足或并发冲突
     */
    @Update("UPDATE sku SET num = num - #{num} WHERE id = #{skuId} AND num >= #{num}")
    int decrStock(@Param("skuId") String skuId, @Param("num") Integer num);
}
