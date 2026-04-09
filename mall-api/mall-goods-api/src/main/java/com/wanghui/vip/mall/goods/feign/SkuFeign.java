package com.wanghui.vip.mall.goods.feign;

import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.cart.model.Cart;
import com.wanghui.vip.mall.goods.model.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FeignClient(value = "mall-goods")
public interface SkuFeign {

    /**
     * 库存扣减请求DTO（内部类）
     */
    class DecrStockRequest {
        private String skuId;
        private Integer num;

        public DecrStockRequest() {}
        public DecrStockRequest(String skuId, Integer num) {
            this.skuId = skuId;
            this.num = num;
        }

        public String getSkuId() { return skuId; }
        public void setSkuId(String skuId) { this.skuId = skuId; }
        public Integer getNum() { return num; }
        public void setNum(Integer num) { this.num = num; }
    }

    @PostMapping(value = "/api/sku/dcount")
    RespResult dcount(@RequestBody List<Cart> carts);

    @GetMapping(value = "/api/sku/{id}")
    RespResult<Sku> one(@PathVariable(value = "id")String id);

    /**
     * 根据SPU ID查询第一个SKU（用于SPU直接添加购物车场景）
     * @param spuId SPU ID
     * @return 第一个SKU
     */
    @GetMapping(value = "/api/sku/spu/{spuId}")
    RespResult<Sku> oneBySpuId(@PathVariable(value = "spuId")String spuId);

    @GetMapping(value = "/api/sku/aditems/type")
    List<Sku> typeItems(@RequestParam(value = "id")Integer id);

    @DeleteMapping(value = "/api/sku/aditems/type")
    RespResult delTypeItems(@RequestParam(value = "id")Integer id);

    @PutMapping(value = "/api/sku/aditems/type")
    RespResult updateTypeItems(@RequestParam(value = "id")Integer id);

    /**
     * 【绝对物理防超卖】库存扣减
     * 供订单服务调用，实现原子性库存扣减
     * @param request 包含 skuId 和 num
     * @return 扣减结果
     */
    @PostMapping(value = "/api/sku/decr")
    RespResult decrStock(@RequestBody DecrStockRequest request);

}
