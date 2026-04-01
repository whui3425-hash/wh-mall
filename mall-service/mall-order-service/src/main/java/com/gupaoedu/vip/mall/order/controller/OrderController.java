package com.gupaoedu.vip.mall.order.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.gupaoedu.mall.util.RespCode;
import com.gupaoedu.mall.util.RespResult;
import com.gupaoedu.vip.mall.order.model.Order;
import com.gupaoedu.vip.mall.order.model.OrderRefund;
import com.gupaoedu.vip.mall.order.service.OrderService;
import com.gupaoedu.vip.mall.pay.WeixinPayParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping(value = "/order")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WeixinPayParam weixinPayParam;

    /****
     * Apply for order cancellation (simulate refund order for testing)
     */
    @PutMapping(value = "/refund/{id}")
    public RespResult refund(@PathVariable(value = "id")String id,HttpServletRequest request) throws Exception{
        //Username
        String username = "gp";
        //Query order to check if it meets refund requirements
        Order order = orderService.getById(id);
        if(order.getPayStatus().intValue()==1 && order.getOrderStatus().intValue()==1){
            //Add refund record, update order status
            OrderRefund orderRefund = new OrderRefund(
                    IdWorker.getIdStr(),
                    id,
                    1,
                    null,
                    username,
                    0,//Apply for refund
                    new Date(),
                    order.getMoneys()
            );
            orderService.refund(orderRefund);

            //Simplified version returns success directly, should call payment service Feign interface in practice
            return RespResult.ok();
        }
        //Return error if conditions not met
        return RespResult.error("Current order does not meet cancellation requirements!");
    }


    /***
     * Add order
     */
    @PostMapping
    public RespResult add(@RequestBody Order order, HttpServletRequest request) throws Exception {
        //Username
        order.setUsername("gp");
        //Place order
        Boolean bo = orderService.add(order);
        String ciptxt = weixinPayParam.weixinParam(order, request);
        return bo? RespResult.ok(ciptxt) : RespResult.error(RespCode.ERROR);
    }


}
