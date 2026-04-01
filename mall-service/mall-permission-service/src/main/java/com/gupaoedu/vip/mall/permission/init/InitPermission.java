package com.gupaoedu.vip.mall.permission.init;

import com.gupaoedu.vip.mall.permission.model.Permission;
import com.gupaoedu.vip.mall.permission.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InitPermission  implements ApplicationRunner {

    @Autowired
    private PermissionService permissionService;

    /***
     * Permission initialization loading
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        //Load all permissions: 0 = exact match filter, 1 = wildcard match
        List<Permission> permissionMatch0 = permissionService.findByMatch(0);
        List<Permission> permissionMatch1 = permissionService.findByMatch(1);

        //All role permissions: query role-permission mapping
        List<Map<Integer, Integer>> rolePermissions = permissionService.allRolePermissions();
        //Match permission list for each role
        Map<String, Set<Permission>> roleMap = rolePermissionFilter(rolePermissions, permissionMatch0, permissionMatch1);
    }

    /****
     * Permissions for each role
     */
    public Map<String, Set<Permission>> rolePermissionFilter(List<Map<Integer, Integer>> rolePermissions,
                                     List<Permission> permissionMatch0,
                                     List<Permission> permissionMatch1){
        //Store which permissions each role has in map
        //Match 0  Match 1
        Map<String, Set<Permission>> rolePermissionMapping = new HashMap<String,Set<Permission>>();

        //Loop through all role relationship mappings
        for (Map<Integer, Integer> rolePermissionMap : rolePermissions) {
            //Role ID
            Integer rid = rolePermissionMap.get("rid");
            //Permission ID
            Integer pid = rolePermissionMap.get("pid");

            //Define a Key
            String key0="Role_0_"+rid;
            String key1="Role_1_"+rid;

            Set<Permission> permissionsSet0 = rolePermissionMapping.get(key0);
            Set<Permission> permissionsSet1 = rolePermissionMapping.get(key1);
            permissionsSet0=permissionsSet0==null? new HashSet<Permission>() : permissionsSet0;
            permissionsSet1=permissionsSet1==null? new HashSet<Permission>() : permissionsSet1;

            //Find permissions for each role - exact match
            for (Permission permission : permissionMatch0) {
                if(permission.getId().intValue()==pid.intValue()){
                    //Permission match complete
                    permissionsSet0.add(permission);
                    break;
                }
            }
            //Find permissions for each role - wildcard match
            for (Permission permission : permissionMatch1) {
                if(permission.getId().intValue()==pid.intValue()){
                    //Permission match complete
                    permissionsSet1.add(permission);
                    break;
                }
            }

            if(permissionsSet0.size()>0){
                rolePermissionMapping.put(key0,permissionsSet0);
            }
            if(permissionsSet1.size()>0){
                rolePermissionMapping.put(key1,permissionsSet1);
            }
        }
        return rolePermissionMapping;
    }
}
