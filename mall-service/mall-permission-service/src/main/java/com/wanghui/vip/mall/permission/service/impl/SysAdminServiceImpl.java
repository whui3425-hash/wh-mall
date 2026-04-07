package com.wanghui.vip.mall.permission.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanghui.vip.mall.permission.mapper.SysAdminMapper;
import com.wanghui.vip.mall.permission.model.SysAdmin;
import com.wanghui.vip.mall.permission.service.SysAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysAdminServiceImpl extends ServiceImpl<SysAdminMapper, SysAdmin> implements SysAdminService {

    @Autowired
    private SysAdminMapper sysAdminMapper;

    @Override
    public SysAdmin findByUsername(String username) {
        return sysAdminMapper.selectByUsername(username);
    }

    @Override
    public SysAdmin login(String username, String password) {
        SysAdmin admin = findByUsername(username);
        if (admin != null && admin.getPassword() != null && admin.getPassword().equals(password)) {
            return admin;
        }
        return null;
    }
}
