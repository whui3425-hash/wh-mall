package com.wanghui.vip.mall.goods.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "brand")
public class Brand implements Serializable {

    @TableId(type= IdType.AUTO)
    private Integer id;
    private String name;
    private String image;
    private String initial;
    private Integer sort;

    @TableField(exist = false)
    private List<Category> categories;
}

