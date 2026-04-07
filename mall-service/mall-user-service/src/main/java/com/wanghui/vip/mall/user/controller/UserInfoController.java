package com.wanghui.vip.mall.user.controller;

import com.wanghui.mall.util.JwtToken;
import com.wanghui.mall.util.MD5;
import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.user.model.UserInfo;
import com.wanghui.vip.mall.user.service.UserInfoService;
import com.wanghui.vip.mall.util.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/user")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * C端买家登录接口（JSON格式）
     * 与 B 端管理员登录完全独立
     * @param loginRequest 登录请求（username + password）
     * @return RespResult 包含 Token、userId、tenantId
     */
    @PostMapping("/login")
    public RespResult<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        // 参数校验
        if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return RespResult.error("用户名或密码不能为空");
        }

        // 查询用户（根据用户名查询）
        UserInfo userInfo = userInfoService.findByUsername(loginRequest.getUsername());

        // 密码比对
        if (userInfo == null || !loginRequest.getPassword().equals(userInfo.getPassword())) {
            return RespResult.error("用户名或密码错误");
        }

        // 【极其重要】构建 JWT Payload，必须包含 user_id 和 tenant_id
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("id", userInfo.getId());              // user_id（C端业务核心）
        tokenData.put("username", userInfo.getUsername());
        tokenData.put("name", userInfo.getName());
        tokenData.put("tenantId", userInfo.getTenantId()); // tenant_id（多租户隔离）
        tokenData.put("roles", userInfo.getRoles());

        // 签发 C 端专用 JWT Token
        String token = JwtToken.createToken(tokenData);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", userInfo.getId());
        result.put("username", userInfo.getUsername());
        result.put("tenantId", userInfo.getTenantId());
        result.put("name", userInfo.getName());

        return RespResult.ok(result);
    }

    /**
     * 登录请求 DTO
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

    // ================== 原有接口（保留兼容）==================

    /**
     * 原有登录接口（保留兼容，使用 RequestParam）
     * http://localhost:8088/user/info/login
     */
    @PostMapping("/info/login")
    public RespResult<String> loginLegacy(@RequestParam(value = "username") String username,
                                          @RequestParam(value = "pwd") String pwd,
                                          HttpServletRequest request) throws Exception {
        // Query user
        UserInfo userinfo = userInfoService.getById(username);
        // Match
        if (userinfo != null && pwd.equals(userinfo.getPassword())) {
            // Create token
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("username", userinfo.getUsername());
            dataMap.put("name", userinfo.getName());
            dataMap.put("roles", userinfo.getRoles());
            // Get IP
            String ip = IPUtils.getIpAddr(request);
            dataMap.put("ip", MD5.md5(ip));
            // Create JWT token
            String token = JwtToken.createToken(dataMap);
            return RespResult.ok(token);
        }
        return RespResult.error("Invalid username or password");
    }
}
