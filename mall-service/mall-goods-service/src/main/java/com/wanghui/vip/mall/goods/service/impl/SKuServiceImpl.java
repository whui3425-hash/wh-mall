package com.wanghui.vip.mall.goods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanghui.vip.mall.cart.model.Cart;
import com.wanghui.vip.mall.goods.mapper.AdItemsMapper;
import com.wanghui.vip.mall.goods.mapper.SkuMapper;
import com.wanghui.vip.mall.goods.model.AdItems;
import com.wanghui.vip.mall.goods.model.Sku;
import com.wanghui.vip.mall.goods.service.SKuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@CacheConfig(cacheNames = "ad-items-skus")
@Service
public class SKuServiceImpl extends ServiceImpl<SkuMapper,Sku> implements SKuService {

    @Autowired
    private AdItemsMapper adItemsMapper;

    @Autowired
    private SkuMapper skuMapper;

    /***
     * Inventory decrease
     * @param carts
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void dcount(List<Cart> carts) {
        for (Cart cart : carts) {
            //Inventory decrease
            int dcount = skuMapper.dcount(cart.getSkuId(), cart.getNum());
            System.out.println("dcount:"+dcount);
            if(dcount<=0){
                throw new RuntimeException("Insufficient inventory!");
            }
        }
    }

    /***
     * Query product list by promotion category ID
     * @param id
     * @return
     * ad-items-skus::1
     */
    @Cacheable(key ="#id" )
    @Override
    public List<Sku> typeSkuItems(Integer id) {
        //1. Query all list info under current category
        QueryWrapper<AdItems> adItemsQueryWrapper = new QueryWrapper<AdItems>();
        adItemsQueryWrapper.eq("type",id);
        List<AdItems> adItems = adItemsMapper.selectList(adItemsQueryWrapper);

        //2. Query product list info based on promotion list
        List<String> skuids = adItems.stream().map(adItem->adItem.getSkuId()).collect(Collectors.toList());
        return skuids==null || skuids.size()<=0? null : skuMapper.selectBatchIds(skuids);
    }

    /***
     * Delete promotion data by category id
     * @param id
     * @return
     */
    @CacheEvict(key ="#id" )
    @Override
    public void delTypeSkuItems(Integer id) {}

    /****
     * Update cache
     * @param id
     * @return
     */
    @CachePut(key = "#id")
    @Override
    public List<Sku> updateTypeSkuItems(Integer id) {
        //1. Query all list info under current category
        QueryWrapper<AdItems> adItemsQueryWrapper = new QueryWrapper<AdItems>();
        adItemsQueryWrapper.eq("type",id);
        List<AdItems> adItems = adItemsMapper.selectList(adItemsQueryWrapper);

        //2. Query product list info based on promotion list
        List<String> skuids = adItems.stream().map(adItem->adItem.getSkuId()).collect(Collectors.toList());
        return skuids==null || skuids.size()<=0? null : skuMapper.selectBatchIds(skuids);
    }

    /**
     * 根据SPU ID获取第一个SKU（用于SPU直接添加购物车场景）
     * @param spuId SPU ID
     * @return 第一个SKU
     */
    @Override
    public Sku getFirstBySpuId(String spuId) {
        List<Sku> skus = skuMapper.selectBySpuId(spuId);
        if (skus != null && !skus.isEmpty()) {
            return skus.get(0);
        }
        return null;
    }

    /**
     * 【绝对物理防超卖】库存扣减
     * 核心机制：使用数据库乐观锁（UPDATE ... WHERE num >= #{num}）
     * 只有当库存充足时（num >= 扣减量），更新才会成功
     * 影响行数 = 0 表示库存不足或被并发抢光
     *
     * @param skuId SKU ID
     * @param num 扣减数量
     * @throws RuntimeException 库存不足时抛出异常
     */
    @Override
    public void decrStock(String skuId, Integer num) {
        // 1. 执行原子性库存扣减（数据库层面乐观锁）
        int affectedRows = skuMapper.decrStock(skuId, num);

        System.out.println("[DecrStock] skuId=" + skuId + ", num=" + num + ", affectedRows=" + affectedRows);

        // 2. 判断扣减结果
        if (affectedRows == 0) {
            // 影响行数为0，说明：
            // a) 库存不足（num < 扣减量）
            // b) 并发冲突（其他线程先扣减了库存）
            throw new RuntimeException("商品库存不足，skuId=" + skuId + ", 请求扣减=" + num);
        }

        // 3. 扣减成功（affectedRows >= 1）
        System.out.println("[DecrStock] 库存扣减成功，skuId=" + skuId);
    }

}
