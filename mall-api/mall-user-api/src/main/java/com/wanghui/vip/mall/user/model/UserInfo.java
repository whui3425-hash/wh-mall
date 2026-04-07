package com.wanghui.vip.mall.user.model;

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
@TableName(value = "user_info")
public class UserInfo implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;           // 用户ID（主键）
    
    private String username;   // 用户名（唯一）
    private String password;   // 密码
    private String phone;      // 手机号
    private String name;       // 昵称
    private Integer points;    // 积分
    private String roles;      // 角色
    private String tenantId;   // 租户ID（多租户隔离）
}
