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
package com.px.pa.modulars.shop.shopUser.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.security.util.wx.WxMaConfiguration;
import com.px.pa.modulars.base.entity.WxConfig;
import com.px.pa.modulars.base.service.WxConfigService;
import com.px.pa.modulars.shop.entity.ShopUser;
import com.px.pa.modulars.shop.mapper.ShopUserMapper;
import com.px.pa.modulars.shop.shopUser.service.ShopUserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 商户信息
 *
 * @author lp
 * @date 2020-11-26 14:32:18
 */
@Service
@Slf4j
public class ShopUserServiceImpl extends ServiceImpl<ShopUserMapper, ShopUser> implements ShopUserService {

    protected Logger logger = LogManager.getLogger();
    @Resource
    private WxConfigService wxConfService;
    @Resource
    private ShopUserService shopUserService;

    @Override
    @Transactional
    public Map<String,Object> loginVali( String code, String nickName, String avatarUrl, String phone) {

        WxConfig wxConfig = wxConfService.list().get(0);
        ShopUser shopUser = null;
        Map<String, Object> result = new HashMap<>();
        try {
            final WxMaService wxService = WxMaConfiguration.getMaService(wxConfig.getAppid(), wxConfig.getSecret());
            try {
                WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(code);
                String wxOpenid = session.getOpenid();
                ShopUser shopUser1 = new ShopUser();
                shopUser1.setOpenid(wxOpenid);
                Wrapper<ShopUser> qw = new QueryWrapper<>(shopUser1);
                ShopUser shopUser2 = shopUserService.getOne(qw);
                //如果用户不存在，则注册用户
                if (shopUser2 == null) {
                    shopUser2 = new ShopUser();
                    shopUser2.setNickname(nickName);
                    shopUser2.setPhoto(avatarUrl);
                    //TODO 获取用户的头像和昵称信息
                    shopUser2.setOpenid(wxOpenid);
                    shopUser2.setPhone(phone);
                    shopUser2.setCreateTime(LocalDateTime.now());
                    shopUser2.setUpdateTime(LocalDateTime.now());
                    shopUserService.save(shopUser2);

                }

            } catch (WxErrorException e) {
                this.logger.error(e.getMessage(), e);
               e.getError();
                // throw new RequestEmptyException("登录失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
