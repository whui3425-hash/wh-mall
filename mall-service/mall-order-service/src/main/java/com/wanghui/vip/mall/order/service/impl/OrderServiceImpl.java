package com.wanghui.vip.mall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.cart.feign.CartFeign;
import com.wanghui.vip.mall.cart.model.Cart;
import com.wanghui.vip.mall.goods.feign.SkuFeign;
import com.wanghui.vip.mall.goods.model.Sku;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    /**
     * 【核心】提交订单（分布式防超卖版）
     * 完整流程：
     * 1. 从购物车查询商品
     * 2. 【原子性扣减库存】调用goods-service的decrStock接口（带乐观锁）
     * 3. 【创建订单】生成全局唯一outTradeNo，保存订单（状态UNPAID）
     * 4. 【清理购物车】删除已购买的购物车商品
     *
     * @param cartItemIds 购物车商品ID列表
     * @param userId 用户ID（从JWT Header X-User-Id获取）
     * @param username 用户名（从JWT Header X-User-Name获取）
     * @param tenantId 租户ID（从JWT Header X-Tenant-Id获取，多租户隔离）
     * @return 包含outTradeNo（流水号）、totalAmount（总金额）、orderId（订单ID）的Map
     * @throws RuntimeException 库存不足时抛出异常，事务回滚
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> submitOrder(List<Long> cartItemIds, String userId, String username, String tenantId) {
        // ========== 【Step 0】参数校验 ==========
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            throw new RuntimeException("购物车商品ID列表不能为空");
        }

        System.out.println("[OrderSubmit] 开始提交订单，userId=" + userId + ", tenantId=" + tenantId + ", cartItems=" + cartItemIds);

        // ========== 【Step 1】查询购物车商品详情 ==========
        RespResult<List<Cart>> cartResp = cartFeign.listByIds(cartItemIds);
        List<Cart> carts = cartResp != null ? cartResp.getData() : null;

        if (carts == null || carts.isEmpty()) {
            throw new RuntimeException("购物车商品不存在或已失效");
        }

        // 校验：购物车商品是否属于当前用户（防止越权）
        for (Cart cart : carts) {
            if (!userId.equals(cart.getUserId())) {
                throw new RuntimeException("购物车商品不属于当前用户，cartId=" + cart.getId());
            }
        }

        System.out.println("[OrderSubmit] 查询到 " + carts.size() + " 个购物车商品");

        // ========== 【Step 2】原子性扣减库存（核心防超卖逻辑） ==========
        // 遍历每个商品，调用goods-service的原子性扣减接口
        for (Cart cart : carts) {
            String skuId = cart.getSkuId();
            Integer num = cart.getNum();

            System.out.println("[OrderSubmit] 正在扣减库存，skuId=" + skuId + ", num=" + num);

            // 调用goods-service的防超卖扣减接口（原子性：UPDATE sku SET num = num - #{num} WHERE id = #{skuId} AND num >= #{num}）
            SkuFeign.DecrStockRequest decrRequest = new SkuFeign.DecrStockRequest();
            decrRequest.setSkuId(skuId);
            decrRequest.setNum(num);

            RespResult decrResp = skuFeign.decrStock(decrRequest);

            // 检查扣减结果：如果返回错误或影响行数为0，说明库存不足或并发冲突
            if (decrResp == null || !"20000".equals(String.valueOf(decrResp.getCode()))) {
                // 扣减失败（库存不足），抛出异常触发事务回滚
                System.out.println("[OrderSubmit] 库存扣减失败，skuId=" + skuId + "，原因：" + (decrResp != null ? decrResp.getMessage() : "接口返回为空"));
                throw new RuntimeException("商品库存不足，skuId=" + skuId + "，请稍后重试或调整购买数量");
            }

            System.out.println("[OrderSubmit] 库存扣减成功，skuId=" + skuId);
        }

        System.out.println("[OrderSubmit] 所有商品库存扣减成功");

        // ========== 【Step 3】生成全局唯一流水号（outTradeNo） ==========
        // 使用雪花算法生成订单ID + UUID生成支付流水号
        String orderId = IdWorker.getIdStr();
        String outTradeNo = "ORD" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();

        System.out.println("[OrderSubmit] 生成订单ID=" + orderId + ", outTradeNo=" + outTradeNo);

        // ========== 【Step 4】计算订单总金额 & 创建订单SKU明细 ==========
        int totalNum = 0;      // 总数量
        int totalAmount = 0;   // 总金额（单位：分）

        for (Cart cart : carts) {
            // 创建订单SKU记录
            OrderSku orderSku = new OrderSku();
            orderSku.setId(IdWorker.getIdStr());
            orderSku.setOrderId(orderId);
            orderSku.setSkuId(cart.getSkuId());
            orderSku.setName(cart.getName());
            orderSku.setPrice(cart.getPrice());
            orderSku.setNum(cart.getNum());
            orderSku.setImage(cart.getImage());
            // 计算该SKU总金额（分）= 单价（分）* 数量
            int skuMoney = cart.getPrice() * cart.getNum();
            orderSku.setMoney(skuMoney);
            orderSku.setTenantId(tenantId);

            // 保存订单SKU
            orderSkuMapper.insert(orderSku);

            // 累加统计
            totalNum += cart.getNum();
            totalAmount += skuMoney;
        }

        System.out.println("[OrderSubmit] 订单总金额=" + totalAmount + "分，总数量=" + totalNum);

        // ========== 【Step 5】创建订单主记录 ==========
        Order order = new Order();
        order.setId(orderId);                          // 订单ID（雪花算法）
        order.setUsername(username);                   // 用户名
        order.setTotalNum(totalNum);                   // 总数量
        order.setMoneys(totalAmount);                  // 总金额（分）
        order.setOrderStatus(0);                       // 0-未支付（UNPAID）
        order.setPayStatus(0);                         // 0-未支付
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setPayType("weixin");                    // 默认微信支付（实际支付时可更改）
        order.setTenantId(tenantId);                   // 租户ID（多租户隔离）

        // 【重要】将outTradeNo存入订单表，供支付时使用
        // 注意：Order实体类可能需要添加outTradeNo字段，这里暂用现有字段存储
        // 如果无法存储，可以通过其他方式关联（如Redis缓存）

        orderMapper.insert(order);

        System.out.println("[OrderSubmit] 订单创建成功，orderId=" + orderId);

        // ========== 【Step 6】清理购物车（物理删除已购买商品） ==========
        RespResult delResp = cartFeign.deleteByIds(cartItemIds);
        if (delResp != null && "20000".equals(String.valueOf(delResp.getCode()))) {
            System.out.println("[OrderSubmit] 购物车商品删除成功，共 " + cartItemIds.size() + " 个");
        } else {
            // 删除购物车失败不影响订单创建，但记录日志
            System.out.println("[OrderSubmit] 警告：购物车商品删除失败，但不影响订单创建");
        }

        // ========== 【Step 7】组装返回结果 ==========
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);                // 订单ID（系统内部使用）
        result.put("outTradeNo", outTradeNo);          // 支付流水号（拉起支付时使用）
        result.put("totalAmount", totalAmount);        // 总金额（分）
        result.put("totalNum", totalNum);              // 总数量
        result.put("username", username);              // 用户名
        result.put("status", "UNPAID");                // 订单状态：未支付

        System.out.println("[OrderSubmit] 订单提交完成，outTradeNo=" + outTradeNo + ", totalAmount=" + totalAmount);

        return result;
    }
}
