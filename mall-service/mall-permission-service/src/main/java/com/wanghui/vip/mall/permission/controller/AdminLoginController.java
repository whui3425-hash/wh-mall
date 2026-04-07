package com.wanghui.vip.mall.permission.controller;

import com.wanghui.mall.util.JwtToken;
import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.permission.model.SysAdmin;
import com.wanghui.vip.mall.permission.service.SysAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员登录控制器 - 支持多租户SaaS登录
 */
@RestController
@RequestMapping(value = "/api/permission/admin")
@CrossOrigin
public class AdminLoginController {

    @Autowired
    private SysAdminService sysAdminService;

    /**
     * 管理员登录接口
     * @param loginRequest 登录请求参数
     * @return RespResult 包含Token、username和tenant_id的响应结果
     */
    @PostMapping(value = "/login")
    public RespResult<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        // 参数校验
        if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return RespResult.error("用户名或密码不能为空");
        }

        // 登录验证
        SysAdmin admin = sysAdminService.login(loginRequest.getUsername(), loginRequest.getPassword());

        if (admin == null) {
            return RespResult.error("用户名或密码错误");
        }

        // 构建JWT载荷，包含租户ID - 【极其重要】用于后续网关解析租户信息
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("id", admin.getId());
        tokenData.put("username", admin.getUsername());
        tokenData.put("tenantId", admin.getTenantId()); // 将tenant_id放入JWT载荷

        // 生成JWT Token
        String token = JwtToken.createToken(tokenData);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("username", admin.getUsername());
        result.put("tenantId", admin.getTenantId());

        return RespResult.ok(result);
    }

    /**
     * 登录请求DTO
     */
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
