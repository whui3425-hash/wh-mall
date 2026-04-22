package com.wanghui.vip.mall.user.controller;

import com.wanghui.mall.util.JwtToken;
import com.wanghui.mall.util.MD5;
import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.user.config.tenant.TenantContextHolder;
import com.wanghui.vip.mall.user.model.UserInfo;
import com.wanghui.vip.mall.user.service.UserInfoService;
import com.wanghui.vip.mall.util.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/user")
public class UserInfoController {

    /**
     * 压测专用永久验证码（用于固定验证码压测场景）
     */
    private static final String STRESS_TEST_CAPTCHA = "PERF-TEST";

    /**
     * 当前演示环境允许的租户（与 C 端域名 shop1/shop2、网关解析一致）
     */
    private static final Set<String> ALLOWED_TENANT_IDS = new HashSet<>(Arrays.asList("1001", "1002"));

    @Autowired
    private UserInfoService userInfoService;

    /**
     * C端买家登录接口（JSON格式）
     * 与 B 端管理员登录完全独立
     * @param loginRequest 登录请求（username + password + captcha）
     * @return RespResult 包含 Token、userId、tenantId
     */
    @PostMapping("/login")
    public RespResult<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        // 参数校验
        if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null
                || loginRequest.getCaptcha() == null) {
            return RespResult.error("用户名、密码或验证码不能为空");
        }

        String captcha = loginRequest.getCaptcha().trim();
        if (captcha.isEmpty()) {
            return RespResult.error("验证码不能为空");
        }
        // 当前版本启用固定验证码：PERF-TEST（永久有效，便于压测）
        if (!STRESS_TEST_CAPTCHA.equalsIgnoreCase(captcha)) {
            return RespResult.error("验证码错误");
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
     * C 端买家注册（JSON）
     * 验证码与登录相同，固定为 {@link #STRESS_TEST_CAPTCHA}，便于演示与压测。
     */
    @PostMapping("/register")
    public RespResult<Map<String, Object>> register(@RequestBody RegisterRequest body) {
        if (body == null || body.getUsername() == null || body.getPassword() == null
                || body.getCaptcha() == null || body.getPhone() == null) {
            return RespResult.error("用户名、密码、手机号或验证码不能为空");
        }
        String username = body.getUsername().trim();
        String password = body.getPassword();
        String captcha = body.getCaptcha().trim();
        String phone = body.getPhone().trim().replaceAll("\\s+", "");
        if (username.isEmpty() || password.isEmpty()) {
            return RespResult.error("用户名或密码不能为空");
        }
        if (phone.isEmpty()) {
            return RespResult.error("手机号不能为空");
        }
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            return RespResult.error("手机号格式不正确（需 11 位中国大陆号码）");
        }
        if (username.length() < 3 || username.length() > 50) {
            return RespResult.error("用户名长度需在 3～50 个字符");
        }
        if (password.length() < 6) {
            return RespResult.error("密码至少 6 位");
        }
        if (captcha.isEmpty()) {
            return RespResult.error("验证码不能为空");
        }
        if (!STRESS_TEST_CAPTCHA.equalsIgnoreCase(captcha)) {
            return RespResult.error("验证码错误");
        }
        if (userInfoService.findByUsername(username) != null) {
            return RespResult.error("该用户名在当前店铺已被占用");
        }
        if (userInfoService.findByPhone(phone) != null) {
            return RespResult.error("该手机号已在当前店铺注册");
        }

        // 租户来自请求头 X-Tenant-Id（TenantWebInterceptor → TenantContextHolder），须为允许值
        String tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null || tenantId.isEmpty() || !ALLOWED_TENANT_IDS.contains(tenantId)) {
            return RespResult.error("缺少或无效的租户，请从对应店铺域名访问并携带 X-Tenant-Id");
        }

        UserInfo user = new UserInfo();
        user.setUsername(username);
        user.setPassword(password);
        String displayName = body.getName() != null ? body.getName().trim() : "";
        user.setName(displayName.isEmpty() ? username : displayName);
        user.setPhone(phone);
        user.setPoints(0);
        user.setRoles("USER");
        user.setTenantId(tenantId);

        boolean saved = userInfoService.save(user);
        if (!saved) {
            return RespResult.error("注册失败，请稍后重试");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("tenantId", user.getTenantId());
        return RespResult.ok(result);
    }

    /**
     * 注册请求 DTO
     */
    public static class RegisterRequest {
        private String username;
        private String password;
        /** 昵称，可选，默认与用户名相同 */
        private String name;
        /** 手机号（11 位，中国大陆） */
        private String phone;
        private String captcha;

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCaptcha() {
            return captcha;
        }

        public void setCaptcha(String captcha) {
            this.captcha = captcha;
        }
    }

    /**
     * 登录请求 DTO
     */
    public static class LoginRequest {
        private String username;
        private String password;
        private String captcha;

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

        public String getCaptcha() {
            return captcha;
        }

        public void setCaptcha(String captcha) {
            this.captcha = captcha;
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
