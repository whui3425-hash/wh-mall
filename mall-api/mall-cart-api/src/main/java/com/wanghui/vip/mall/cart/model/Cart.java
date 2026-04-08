package com.wanghui.vip.mall.cart.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("mall_cart")
public class Cart implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String userId;       // 用户ID（从JWT解析）
    private String userName;     // 用户名
    private String name;         // 商品名称
    private Integer price;       // 商品价格（单位：分）
    private String image;        // 商品图片
    private String skuId;        // SKU ID
    private Integer num;         // 购买数量
    private String tenantId;     // 租户ID（多租户隔离）
}
