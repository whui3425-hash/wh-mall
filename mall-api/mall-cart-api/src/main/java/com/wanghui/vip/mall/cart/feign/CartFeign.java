package com.wanghui.vip.mall.cart.feign;

import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.cart.model.Cart;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "mall-cart")
public interface CartFeign {

    @DeleteMapping(value = "/cart")
    RespResult delete(@RequestBody List<String> ids);

    @PostMapping(value = "/cart/list")
    RespResult<List<Cart>> list(@RequestBody List<String> ids);
}

