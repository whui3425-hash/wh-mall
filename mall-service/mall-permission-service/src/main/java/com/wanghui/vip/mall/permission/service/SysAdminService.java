package com.wanghui.vip.mall.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanghui.vip.mall.permission.model.SysAdmin;

public interface SysAdminService extends IService<SysAdmin> {

    /**
     * 根据用户名查询管理员
     * @param username 用户名
     * @return SysAdmin 管理员信息
     */
    SysAdmin findByUsername(String username);

    /**
     * 管理员登录验证
     * @param username 用户名
     * @param password 密码
     * @return SysAdmin 验证成功返回管理员信息，失败返回 null
     */
    SysAdmin login(String username, String password);
}
