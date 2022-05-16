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

package com.px.pa.modulars.shop.shopUser.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pig4cloud.pig.common.core.base.BaseApiController;
import com.pig4cloud.pig.common.core.support.cache.RedisHelper;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.Inner;
import com.pig4cloud.pig.common.security.jwt.JwtTokenUtil;
import com.pig4cloud.pig.common.security.jwt.payload.JwtPayLoad;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.pig.common.security.util.wx.WxMaConfiguration;
import com.px.msg.MessageService;
import com.px.pa.constants.Constants;
import com.px.pa.modulars.base.entity.WxConfig;
import com.px.pa.modulars.base.service.WxConfigService;
import com.px.pa.modulars.examine.service.ExamineTaskService;
import com.px.pa.modulars.examine.vo.ShopTaskCenterResult;
import com.px.pa.modulars.shop.entity.ShopUser;
import com.px.pa.modulars.shop.shopUser.service.ShopUserService;
import com.px.pa.modulars.sysConfigSms.service.SysConfigSmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;


/**
 * 商户信息
 *
 * @author lp
 * @date 2020-11-26 14:32:18
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shop")
@Api(value = "shop", tags = "商户信息服务")
@Inner(false)
public class ShopApiController extends BaseApiController {

    private final ShopUserService shopUserService;

    private final ExamineTaskService taskService;

    private final RedisHelper redisHelper;
    private WxMaService wxMaService;

    @GetMapping("/current")
    @ApiOperation("获取当前信息")
    public R getCurrent() {
        Integer id = super.getCurrentId();
        ShopUser shop = this.shopUserService.getById(id);
        return R.ok(shop);
    }

    @GetMapping("/task/center")
    @ApiOperation("获取当前用户的任务大厅信息")
    public R getCurrentTaskCenter() {
        Integer userId = super.getCurrentId();
        Date now = new Date();
        ShopTaskCenterResult result = this.taskService.readTaskCenterByShop(userId, DateUtil.beginOfDay(now), DateUtil.endOfDay(now));
        return R.ok(result);
    }

    @GetMapping("/logout")
    @ApiOperation("登出")
    public R logout() {
        Integer userId=super.getCurrentId();
        ShopUser user=this.shopUserService.getById(userId);
        user.setOpenid("");
        this.shopUserService.updateById(user);
        String token = request.getHeader(Constants.Auth.TOKEN_KEY);
        this.redisHelper.hdel(Constants.Auth.TOKEN_CACHE_KEY, token);
        return R.ok();
    }

    /**
     * 默认登录
     *
     * @param
     * @return
     */
    @ApiOperation(value = "默认登录", notes = "默认登录")
    @GetMapping("/default/login" )
    @Inner(value = false)
    public R findPassword(@RequestParam("code") String code) {

        WxMaJscode2SessionResult session = null;
        try {
            session = this.wxMaService.getUserService().getSessionInfo(code);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        String openId = session.getOpenid();
        ShopUser shopUser = new ShopUser();
        shopUser.setOpenid(openId);
        Wrapper<ShopUser> qw = new QueryWrapper<>(shopUser);
        ShopUser shopUser1 = shopUserService.getOne(qw);


        if(shopUser1 != null){
            JwtPayLoad payLoad = new JwtPayLoad(Long.parseLong(shopUser1.getId().toString()), shopUser1.getOpenid(), "xxxx");

            //创建token
            String token = JwtTokenUtil.generateToken(payLoad);

            this.redisHelper.hset(Constants.Auth.TOKEN_CACHE_KEY, token, shopUser1.getId());
            HashMap<String,Object> map = new HashMap<>();
            map.put("shopUser",shopUser1);
            map.put("token",token);
            return R.ok(map);
        }else {
            return R.failed(null,"此用户未登录，请重新登录");
        }
    }

    @Autowired
    private WxConfigService wxConfigService;

    private WxMaService getWxService() {
        if (this.wxMaService == null) {
            WxConfig wxConfig = wxConfigService.list().get(0);
            this.wxMaService = WxMaConfiguration.getMaService(wxConfig.getAppid(), wxConfig.getSecret());
        }
        return this.wxMaService;
    }


}
