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
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pig4cloud.pig.common.core.support.cache.RedisHelper;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.Inner;
import com.pig4cloud.pig.common.security.jwt.JwtTokenUtil;
import com.pig4cloud.pig.common.security.jwt.payload.JwtPayLoad;
import com.pig4cloud.pig.common.security.util.wx.WxMaConfiguration;
import com.px.msg.MessageService;
import com.px.pa.constants.Constants;
import com.px.pa.modulars.base.entity.WxConfig;
import com.px.pa.modulars.base.service.WxConfigService;
import com.px.pa.modulars.examine.entity.ExamineTask;
import com.px.pa.modulars.examine.service.ExamineTaskService;
import com.px.pa.modulars.shop.entity.ShopUser;
import com.px.pa.modulars.shop.shopUser.service.ShopUserService;
import com.px.pa.modulars.shop.shopUser.vo.ShopUserVo;
import com.px.pa.modulars.sysConfigSms.service.SysConfigSmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 商户信息
 *
 * @author lp
 * @date 2020-11-26 14:32:18
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/wx")
@Api(value = "shopuser1", tags = "商户信息管理")
public class ShopUserApiController {

    private final ShopUserService shopUserService;
    private final SysConfigSmsService sysConfigSmsService;
    private final RedisTemplate redisTemplate;
    private final RedisHelper redisHelper;
    private final ExamineTaskService examineTaskService;
    private WxMaService wxMaService;
    @Autowired
    private MessageService messageService;


    /**
     * 小程序登录
     *
     * @return R
     */
    @ApiOperation(value = "小程序登录", notes = "小程序登录")
    //@SysLog("新增商户信息" )
    @PostMapping("/login")
    @Inner(value = false)
    public R login(@RequestBody ShopUserVo shopUserVo) {

        String code1 = redisTemplate.opsForValue().get(shopUserVo.getPhone()).toString();
        if (shopUserVo.getCode() == null || shopUserVo.getCode().length() == 0) {
            return R.failed("验证码失效");
        } else {
            if (code1.equals(shopUserVo.getCode().toLowerCase())) {
                //删除验证码
                redisTemplate.delete(shopUserVo.getPhone());
                ShopUser shopUser1 = new ShopUser();
                if (StringUtils.isNotEmpty(shopUserVo.getPhone())) {
                    ShopUser shopUser = new ShopUser();
                    shopUser.setPhone(shopUserVo.getPhone());
                    Wrapper<ShopUser> qw = new QueryWrapper<>(shopUser);
                    shopUser1 = shopUserService.getOne(qw);
                    if (shopUser1 == null) {
                        Map<String, Object> map1 = shopUserService.loginVali(shopUserVo.getWxCode(), shopUserVo.getNickName(), shopUserVo.getAvatarUrl(), shopUser.getPhone());
                        //TODO 注册完成后，根据手机号更新任务，找到用户相关的任务，将任务与用户关联
                        ExamineTask examineTask = new ExamineTask();
                        examineTask.setShopTel(shopUser.getPhone());
                        Wrapper<ExamineTask> qw1 = new QueryWrapper<>(examineTask);
                        List<ExamineTask> examineTaskList = examineTaskService.list(qw1);
                        ShopUser shopUser2 = new ShopUser();
                        shopUser2.setPhone(shopUser.getPhone());
                        ShopUser shopUser3 = shopUserService.getOne(new QueryWrapper<>(shopUser2));
                        final String[] name = {null};
                        final String[] idcard = {null};
                        examineTaskList.forEach(a -> {
                            a.setUser(shopUser3.getId());
                            examineTaskService.updateById(a);
                            name[0] = a.getUserName();
                            idcard[0] = a.getIdCard();
                        });
                        ShopUser shopUser4 = new ShopUser();
                        shopUser4.setPhone(shopUserVo.getPhone());
                        Wrapper<ShopUser> qw2 = new QueryWrapper<>(shopUser4);

                        ShopUser shopUser5 = shopUserService.getOne(qw2);
                        shopUser5.setName(name[0]);
                        shopUser5.setIdCard(idcard[0]);
                        //WxMaJscode2SessionResult session = null;
                       /* try {
                            session = this.getWxService().getUserService().getSessionInfo(shopUserVo.getWxCode());
                        } catch (WxErrorException e) {
                            e.printStackTrace();
                        }
                        String openId = session.getOpenid();*/


                        JwtPayLoad payLoad = new JwtPayLoad(Long.parseLong(shopUser5.getId().toString()), shopUser5.getOpenid(), "xxxx");

                        //创建token
                        String token = JwtTokenUtil.generateToken(payLoad);
                        this.redisHelper.hset(Constants.Auth.TOKEN_CACHE_KEY, token, shopUser5.getId());
                        //更新最后一次登录时间
                        shopUser5.setLastLogintime(LocalDateTime.now());
                        shopUserService.updateById(shopUser5);
                        Map<String, Object> map = new HashMap<>();
                        map.put("shopUser", shopUser5);
                        map.put("token", token);
                        return R.ok(map);
                    } else {
                        WxMaJscode2SessionResult session = null;
                        try {
                            session = this.getWxService().getUserService().getSessionInfo(shopUserVo.getWxCode());
                        } catch (WxErrorException e) {
                            e.printStackTrace();
                        }
                        String openId = session.getOpenid();


                        JwtPayLoad payLoad = new JwtPayLoad(Long.parseLong(shopUser1.getId().toString()), openId, "xxxx");

                        //创建token
                        String token = JwtTokenUtil.generateToken(payLoad);
                        this.redisHelper.hset(Constants.Auth.TOKEN_CACHE_KEY, token, shopUser1.getId());
                        //更新最后一次登录时间
                        ShopUser shopUser2 = new ShopUser();
                        BeanUtil.copyProperties(shopUser1, shopUser2);
                        shopUser2.setLastLogintime(LocalDateTime.now());
                        shopUser2.setOpenid(openId);
                        shopUserService.updateById(shopUser2);

                        Map<String, Object> map = new HashMap<>();
                        map.put("shopUser", shopUser2);
                        map.put("token", token);
                        return R.ok(map);
                    }
                } else {
                    return R.failed("手机号不能为空");
                }
            } else {
                return R.failed("手机验证码不正确");
            }
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

    /**
     * 查询商户列表
     */
    @ApiOperation(value = "查询商户列表", notes = "查询商户列表")
    @GetMapping("/select")
    @Inner(value = false)
    public R getById() {

        return R.ok(shopUserService.list());
    }

}
