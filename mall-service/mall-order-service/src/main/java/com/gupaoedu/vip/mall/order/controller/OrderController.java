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

/*****
 * @Author:
 * @Description:
 ****/
@RestController
@RequestMapping(value = "/order")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WeixinPayParam weixinPayParam;

    // TODO: SaaS极简版，后续替换为普通的 Feign 同步调用或本地逻辑
    // @Autowired
    // private RocketMQTemplate rocketMQTemplate;

    /****
     * 申请取消订单（模拟测试退款的订单）
     */
    @PutMapping(value = "/refund/{id}")
    public RespResult refund(@PathVariable(value = "id")String id,HttpServletRequest request) throws Exception{
        //用户名
        String username = "gp";
        //查询订单，是否符合退款要求
        Order order = orderService.getById(id);
        if(order.getPayStatus().intValue()==1 && order.getOrderStatus().intValue()==1){
            //添加退款记录,更新订单状态
            OrderRefund orderRefund = new OrderRefund(
                    IdWorker.getIdStr(),
                    id,
                    1,
                    null,
                    username,
                    0,//申请退款
                    new Date(),
                    order.getMoneys()
            );
            orderService.refund(orderRefund);

            // TODO: SaaS极简版，后续替换为普通的 Feign 同步调用或本地逻辑
            // 原代码：向MQ发消息（申请退款）
            // Message message = MessageBuilder.withPayload(weixinPayParam.weixinRefundParam(orderRefund)).build();
            // TransactionSendResult transactionSendResult = rocketMQTemplate.sendMessageInTransaction("refundtx", "refund", message, orderRefund);
            // if(transactionSendResult.getSendStatus()== SendStatus.SEND_OK){
            //     return RespResult.ok();
            // }

            // 简化版直接返回成功，实际应调用支付服务Feign接口
            return RespResult.ok();
        }
        //不符合直接返回错误
        return RespResult.error("当前订单不符合取消操作要求！");
    }


    /***
     * 添加订单
     */
    @PostMapping
    public RespResult add(@RequestBody Order order, HttpServletRequest request) throws Exception {
        //用户名字
        order.setUsername("gp");
        //下单
        Boolean bo = orderService.add(order);
        String ciptxt = weixinPayParam.weixinParam(order, request);
        return bo? RespResult.ok(ciptxt) : RespResult.error(RespCode.ERROR);
    }


}
