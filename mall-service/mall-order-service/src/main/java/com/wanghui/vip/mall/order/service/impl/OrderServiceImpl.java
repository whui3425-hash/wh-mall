package com.wanghui.vip.mall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gupaoedu.mall.util.RespResult;
import com.wanghui.vip.mall.cart.feign.CartFeign;
import com.wanghui.vip.mall.cart.model.Cart;
import com.wanghui.vip.mall.goods.feign.SkuFeign;
import com.wanghui.vip.mall.order.mapper.OrderMapper;
import com.wanghui.vip.mall.order.mapper.OrderRefundMapper;
import com.wanghui.vip.mall.order.mapper.OrderSkuMapper;
import com.wanghui.vip.mall.order.model.Order;
import com.wanghui.vip.mall.order.model.OrderRefund;
import com.wanghui.vip.mall.order.model.OrderSku;
import com.wanghui.vip.mall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper,Order> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderSkuMapper orderSkuMapper;

    @Autowired
    private CartFeign cartFeign;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private OrderRefundMapper orderRefundMapper;

    /****
     * Refund application
     * @param orderRefund
     * @return
     */
    @Override
    public int refund(OrderRefund orderRefund) {
        //1. Record refund application
        orderRefundMapper.insert(orderRefund);

        //2. Update order status
        Order order = new Order();
        order.setOrderStatus(4);    //Apply for refund

        //Build conditions
        QueryWrapper<Order> queryWrapper = new QueryWrapper<Order>();
        queryWrapper.eq("id",orderRefund.getOrderNo()); //Order ID
        queryWrapper.eq("username",orderRefund.getUsername()); //Username
        queryWrapper.eq("order_status",1);
        queryWrapper.eq("pay_status",1);
        int count = orderMapper.update(order, queryWrapper);
        return count;
    }

    /***
     * Add order
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean add(Order order) {
        //Complete data
        order.setId(IdWorker.getIdStr());   //ID
        order.setCreateTime(new Date());    //Create time
        order.setUpdateTime(order.getCreateTime());

        //1. Query shopping cart data
        RespResult<List<Cart>> cartResp = cartFeign.list(order.getCartIds());
        List<Cart> carts = cartResp.getData();
        if(carts==null || carts.size()==0){
            return false;
        }

        //2. Decrease inventory
        skuFeign.dcount(carts);

        //3. Add order details
        int totalNum=0;
        int moneys = 0;
        for (Cart cart : carts) {
            //Convert Cart to OrderSku
            OrderSku orderSku = JSON.parseObject(JSON.toJSONString(cart), OrderSku.class);
            orderSku.setId(IdWorker.getIdStr());
            orderSku.setOrderId(order.getId()); //Pre-assign
            orderSku.setMoney(orderSku.getPrice()*orderSku.getNum());

            //Add
            orderSkuMapper.insert(orderSku);

            //Statistics calculation
            totalNum +=orderSku.getNum();
            moneys += orderSku.getMoney();
        }

        //4. Add order
        order.setTotalNum(totalNum);
        order.setMoneys(moneys);
        orderMapper.insert(order);

        //Exception--->TestTransaction
        //int q=10/0;

        //5. Delete shopping cart data
        cartFeign.delete(order.getCartIds());
        return true;
    }

    /****
     * Update status after successful payment
     * @param id
     * @return
     */
    @Override
    public int updateAfterPayStatus(String id) {
        //Modified status
        Order order = new Order();
        order.setId(id);
        order.setOrderStatus(1);    // Pending shipment
        order.setPayStatus(1);  //Paid

        //Update conditions
        QueryWrapper<Order> queryWrapper = new QueryWrapper<Order>();
        queryWrapper.eq("id",id);
        queryWrapper.eq("order_status",0);
        queryWrapper.eq("pay_status",0);
        return orderMapper.update(order,queryWrapper);
    }
}
