package com.gupaoedu.vip.mall.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gupaoedu.vip.mall.user.model.Address;

import java.util.List;

public interface AddressService extends IService<Address>{


    /****
     * Query user address list
     */
    List<Address> list(String userName);
}
