package com.wanghui.vip.mall.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wanghui.vip.mall.permission.model.SysAdmin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysAdminMapper extends BaseMapper<SysAdmin> {

    /**
     * 根据用户名查询管理员信息
     * @param username 用户名
     * @return SysAdmin 管理员信息
     */
    @Select("SELECT id, username, password, tenant_id as tenantId FROM sys_admin WHERE username = #{username}")
    SysAdmin selectByUsername(@Param("username") String username);
}
