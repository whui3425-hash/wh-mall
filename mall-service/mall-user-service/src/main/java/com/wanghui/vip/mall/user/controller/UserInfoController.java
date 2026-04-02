package com.wanghui.vip.mall.user.controller;

import com.wanghui.mall.util.JwtToken;
import com.wanghui.mall.util.MD5;
import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.user.model.UserInfo;
import com.wanghui.vip.mall.user.service.UserInfoService;
import com.wanghui.vip.mall.util.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/user/info")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    /****
     * Login http://localhost:8088/user/info/login
     * @param username
     * @param pwd
     * @return
     */
    @PostMapping("/login")
    public RespResult<String> login(@RequestParam(value = "username")String username,
                                    @RequestParam(value = "pwd")String pwd,
                                    HttpServletRequest request) throws Exception{
        //Query user
        UserInfo userinfo = userInfoService.getById(username);
        //Match
        if(userinfo!=null && pwd.equals(userinfo.getPassword())){
            //Create token
            Map<String,Object> dataMap = new HashMap<String,Object>();
            dataMap.put("username",userinfo.getUsername());
            dataMap.put("name",userinfo.getName());
            dataMap.put("roles",userinfo.getRoles());
            //Get IP
            String ip = IPUtils.getIpAddr(request);
            dataMap.put("ip", MD5.md5(ip));
            //Create JWT token
            String token = JwtToken.createToken(dataMap);
            return RespResult.ok(token);
        }
        return RespResult.error("Invalid username or password");
    }

}
