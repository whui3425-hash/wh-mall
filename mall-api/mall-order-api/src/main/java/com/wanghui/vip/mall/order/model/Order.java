package com.wanghui.vip.mall.order.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "order_info")
public class Order  implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String payType;
    private Date createTime;
    private Date updateTime;
    private Date payTime;
    private Date consignTime;
    private Date endTime;
    private String username;
    private String recipients;
    private String recipientsMobile;
    private String recipientsAddress;
    private String weixinTransactionId;
    private Integer totalNum;
    private Integer moneys;
    private Integer orderStatus;
    private Integer payStatus;
    private Integer isDelete;
    private String tenantId;        // 【多租户】租户ID，数据隔离
    private String outTradeNo;      // 【支付】外部交易流水号，拉起支付时使用

    @TableField(exist = false)
    private List<String> cartIds;

    @TableField(exist = false)
    private List<Long> cartItemIds;  // 【提交订单】购物车商品ID列表（不持久化）
}
