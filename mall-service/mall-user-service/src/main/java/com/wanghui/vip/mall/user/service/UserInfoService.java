package com.wanghui.vip.mall.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanghui.vip.mall.user.model.UserInfo;

public interface UserInfoService extends IService<UserInfo> {

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return UserInfo 用户信息
     */
    UserInfo findByUsername(String username);

    /**
     * 按手机号查询（当前租户下，由多租户插件自动追加 tenant_id）
     */
    UserInfo findByPhone(String phone);
}
