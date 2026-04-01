package com.gupaoedu.vip.mall.goods.controller;

import com.gupaoedu.mall.util.RespResult;
import com.gupaoedu.vip.mall.cart.model.Cart;
import com.gupaoedu.vip.mall.goods.model.Sku;
import com.gupaoedu.vip.mall.goods.service.SKuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/sku")
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
