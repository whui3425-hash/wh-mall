package com.wanghui.vip.mall.user.controller;

import com.wanghui.mall.util.RespResult;
import com.wanghui.vip.mall.user.model.Address;
import com.wanghui.vip.mall.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/address")
@CrossOrigin
public class AddressController {

    @Autowired
    private AddressService addressService;

    /****
     * User address list
     */
    @GetMapping(value = "/list")
    public RespResult<List<Address>> list(){
        String userName = "gp";
        List<Address> list = addressService.list(userName);
        return RespResult.ok(list);
    }
}
