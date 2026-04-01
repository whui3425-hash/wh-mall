package com.gupaoedu.vip.mall.permission.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gupaoedu.vip.mall.permission.model.Permission;

import java.util.List;
import java.util.Map;

public interface PermissionService extends IService<Permission> {

    /***
     * Query permission list by match method
     */
    List<Permission> findByMatch(Integer matchMethod);

    /****
     * Query all role-permission mappings
     */
    List<Map<Integer,Integer>> allRolePermissions();

}
