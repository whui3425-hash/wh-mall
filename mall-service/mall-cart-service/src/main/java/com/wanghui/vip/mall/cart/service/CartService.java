package com.wanghui.vip.mall.cart.service;

import com.wanghui.vip.mall.cart.model.Cart;

import java.util.List;

public interface CartService {

    /***
     * Delete shopping cart list by IDs
     */
    void delete(List<String> ids);

    /***
     * Query shopping cart list by ID collection
     */
    List<Cart> list(List<String> ids);

    /***
     * Shopping cart list
     */
    List<Cart> list(String userName);

    /***
     * Add to shopping cart
     */
    void add(String id,String userName,Integer num);


}
