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
    private String userName;
    private String name;
    private Integer price;
    private String image;
    private String skuId;
    private Integer num;
    private String tenantId;
}
