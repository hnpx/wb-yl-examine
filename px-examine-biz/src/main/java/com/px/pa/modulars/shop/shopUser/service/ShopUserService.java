/*
 *    Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the pig4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: lengleng (wangiegie@gmail.com)
 */

package com.px.pa.modulars.shop.shopUser.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.px.pa.modulars.shop.entity.ShopUser;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 商户信息
 *
 * @author lp
 * @date 2020-11-26 14:32:18
 */
public interface ShopUserService extends IService<ShopUser> {

    public Map<String,Object> loginVali(String code, String nickName, String avatarUrl, String phone);

    /**
     * 根据手机号查找用户
     * @param phone
     * @return
     */
//    public ShopUser readByPhone(String phone);
}
