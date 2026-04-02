package com.wanghui.vip.mall.cart.controller;

import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.cart.model.Cart;
import com.wanghui.vip.mall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/cart")
@CrossOrigin
public class CartController {

    @Autowired
    private CartService cartService;

    /***
     * Delete shopping cart data
     */
    @DeleteMapping
    public RespResult delete(@RequestBody List<String> ids){
        cartService.delete(ids);
        return RespResult.ok();
    }


    /***
     * Shopping cart data by specified ID collection
     * http://localhost:8087/cart/list
     */
    @PostMapping(value = "/list")
    public RespResult<List<Cart>> list(@RequestBody List<String> ids){
        List<Cart> carts = cartService.list(ids);
        return RespResult.ok(carts);
    }

    /****
     * Add to shopping cart
     */
    @GetMapping(value = "/{id}/{num}")
    public RespResult add(@PathVariable(value = "id")String id,
                          @PathVariable(value = "num")Integer num){
        String userName = "gp";
        cartService.add(id,userName,num);
        return RespResult.ok();
    }


    /****
     * Shopping cart list
     */
    @GetMapping(value = "/list")
    public RespResult<List<Cart>> list(){
        String userName = "gp";
        List<Cart> list = cartService.list(userName);
        return RespResult.ok(list);
    }
}
