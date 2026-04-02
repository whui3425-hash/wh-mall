package com.wanghui.vip.mall.goods.feign;

import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.cart.model.Cart;
import com.wanghui.vip.mall.goods.model.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "mall-goods")
public interface SkuFeign {

    @PostMapping(value = "/sku/dcount")
    RespResult dcount(@RequestBody List<Cart> carts);

    @GetMapping(value = "/sku/{id}")
    RespResult<Sku> one(@PathVariable(value = "id")String id);

    @GetMapping(value = "/sku/aditems/type")
    List<Sku> typeItems(@RequestParam(value = "id")Integer id);

    @DeleteMapping(value = "/sku/aditems/type")
    RespResult delTypeItems(@RequestParam(value = "id")Integer id);

    @PutMapping(value = "/sku/aditems/type")
    RespResult updateTypeItems(@RequestParam(value = "id")Integer id);

}

