package com.wanghui.vip.mall.goods.feign;

import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.goods.model.Product;
import com.wanghui.vip.mall.goods.model.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "mall-goods")
public interface SpuFeign {

    @GetMapping(value = "/api/spu/product/{id}")
    RespResult<Product> one(@PathVariable(value = "id")String id);
}
