package com.gupaoedu.vip.mall.goods.model;

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
@TableName(value = "sku_attribute")
public class SkuAttribute implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String options;
    private Integer sort;

    @TableField(exist = false)
    private List<Category> categories;

}
