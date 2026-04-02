package com.wanghui.vip.mall.goods.feign;

import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.goods.model.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "mall-goods")
public interface CategoryFeign {

    @GetMapping(value = "/category/{id}")
    RespResult<Category> one(@PathVariable(value = "id")Integer id);
}

