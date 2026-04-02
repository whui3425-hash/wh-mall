package com.wanghui.vip.mall.goods.controller;

import com.gupaoedu.mall.util.RespResult;
import com.wanghui.vip.mall.goods.model.SkuAttribute;
import com.wanghui.vip.mall.goods.service.SkuAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/skuAttribute")
@CrossOrigin
public class SkuAttributeController {

    @Autowired
    private SkuAttributeService skuAttributeService;

    /*****
     * Query attribute list by category ID
     */
    @GetMapping(value = "/category/{id}")
    public RespResult<List<SkuAttributeController>> categorySkuAttributeList(@PathVariable(value = "id")Integer id){
        List<SkuAttribute> skuAttributes = skuAttributeService.queryList(id);
        return RespResult.ok(skuAttributes);
    }
}
