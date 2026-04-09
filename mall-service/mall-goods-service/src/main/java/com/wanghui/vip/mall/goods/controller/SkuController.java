package com.wanghui.vip.mall.goods.controller;

import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.cart.model.Cart;
import com.wanghui.vip.mall.goods.model.Sku;
import com.wanghui.vip.mall.goods.service.SKuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库存扣减请求DTO
 */
class DecrStockRequest {
    private String skuId;
    private Integer num;

    public String getSkuId() { return skuId; }
    public void setSkuId(String skuId) { this.skuId = skuId; }
    public Integer getNum() { return num; }
    public void setNum(Integer num) { this.num = num; }
}

@RestController
@RequestMapping(value = "/api/sku")
public class SkuController {

    @Autowired
    private SKuService sKuService;


    /***
     * Inventory decrease
     */
    @PostMapping(value = "/dcount")
    public RespResult dcount(@RequestBody List<Cart> carts){
        sKuService.dcount(carts);
        return RespResult.ok();
    }

    /***
     * Query product details by ID
     * @return
     */
    @GetMapping(value = "/{id}")
    public RespResult<Sku> one(@PathVariable(value = "id")String id){
        Sku sku = sKuService.getById(id);
        return RespResult.ok(sku);
    }

    /**
     * 根据SPU ID查询第一个SKU（用于SPU直接添加购物车场景）
     * @param spuId SPU ID
     * @return 第一个SKU
     */
    @GetMapping(value = "/spu/{spuId}")
    public RespResult<Sku> oneBySpuId(@PathVariable(value = "spuId")String spuId){
        Sku sku = sKuService.getFirstBySpuId(spuId);
        return RespResult.ok(sku);
    }

    /**
     * 【绝对物理防超卖】库存扣减接口
     * 供订单服务调用，实现原子性库存扣减
     * 使用数据库乐观锁（UPDATE ... WHERE num >= #{num}）防止并发超卖
     *
     * @param request 包含 skuId 和 num（扣减数量）
     * @return 扣减成功返回 RespResult.ok()，库存不足返回错误
     */
    @PostMapping(value = "/decr")
    public RespResult decrStock(@RequestBody DecrStockRequest request){
        if (request == null || request.getSkuId() == null || request.getNum() == null || request.getNum() <= 0) {
            return RespResult.error("参数错误：skuId和num必须有效");
        }

        try {
            sKuService.decrStock(request.getSkuId(), request.getNum());
            return RespResult.ok("库存扣减成功");
        } catch (RuntimeException e) {
            return RespResult.error(e.getMessage());
        }
    }

    /****
     * Query promotion product list by promotion category
     */
    @GetMapping(value = "/aditems/type")
    public List<Sku> typeItems(@RequestParam(value = "id")Integer id){
        List<Sku> skus = sKuService.typeSkuItems(id);
        return skus;
    }

    /****
     * Delete promotion data by category id
     */
    @DeleteMapping(value = "/aditems/type")
    public RespResult delTypeItems(@RequestParam(value = "id")Integer id){
        sKuService.delTypeSkuItems(id);
        return RespResult.ok();
    }

    /****
     * Update promotion product list by promotion category
     */
    @PutMapping(value = "/aditems/type")
    public RespResult updateTypeItems(@RequestParam(value = "id")Integer id){
        sKuService.updateTypeSkuItems(id);
        return RespResult.ok();
    }

}
