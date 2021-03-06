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
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.support.cache.RedisHelper;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.Inner;
import com.pig4cloud.pig.common.security.jwt.JwtTokenUtil;
import com.pig4cloud.pig.common.security.jwt.payload.JwtPayLoad;
import com.pig4cloud.pig.common.security.util.wx.WxMaConfiguration;
import com.px.msg.IMessageService;
import com.px.msg.MessageService;
import com.px.msg.vo.SendMessageParam;
import com.px.pa.constants.Constants;
import com.px.pa.modulars.base.entity.WxConfig;
import com.px.pa.modulars.base.service.WxConfigService;
import com.px.pa.modulars.examine.entity.ExamineTask;
import com.px.pa.modulars.examine.service.ExamineTaskService;
import com.px.pa.modulars.shop.entity.ShopUser;
import com.px.pa.modulars.shop.shopUser.service.ShopUserService;
import com.px.pa.modulars.sysConfigSms.service.SysConfigSmsService;
import com.px.pa.modulars.upms.controller.UserController;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ????????????
 *
 * @author lp
 * @date 2020-11-26 14:32:18
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/shopuser")
@Api(value = "shopuser", tags = "??????????????????")
public class ShopUserController {

    private final ShopUserService shopUserService;
    private final SysConfigSmsService sysConfigSmsService;
    private final RedisTemplate redisTemplate;
    private final ExamineTaskService examineTaskService;
    @Autowired
    private MessageService messageService;

    /**
     * ????????????
     *
     * @param page     ????????????
     * @param shopUser ????????????
     * @return
     */
    @ApiOperation(value = "????????????", notes = "????????????")
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('shopUser_shopuser_get')")
    public R getShopUserPage(Page page, ShopUser shopUser) {

        return R.ok(shopUserService.page(page, new QueryWrapper<ShopUser>().like(shopUser.getName() != null, "name", shopUser.getName())
                .like(shopUser.getPhone() != null, "phone", shopUser.getPhone())
        ));
    }


    /**
     * ??????id??????????????????
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "??????id??????", notes = "??????id??????")
    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('shopUser_shopuser_get')")
    public R getById(@PathVariable("id") Integer id) {
        return R.ok(shopUserService.getById(id));
    }

    /**
     * ??????????????????
     *
     * @param shopUser ????????????
     * @return R
     */
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    //@SysLog("??????????????????" )
    @PostMapping
    @PreAuthorize("@pms.hasPermission('shopUser_shopuser_add')")
    public R save(@RequestBody ShopUser shopUser) {
        return R.ok(shopUserService.save(shopUser));
    }

    /**
     * ??????????????????
     *
     * @param shopUser ????????????
     * @return R
     */
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    //@SysLog("??????????????????" )
    @PutMapping
    @PreAuthorize("@pms.hasPermission('shopUser_shopuser_edit')")
    public R updateById(@RequestBody ShopUser shopUser) {
        return R.ok(shopUserService.updateById(shopUser));
    }

    /**
     * ??????id??????????????????
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "??????id??????????????????", notes = "??????id??????????????????")
    //@SysLog("??????id??????????????????" )
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('shopUser_shopuser_del')")
    public R removeById(@PathVariable Integer id) {
        return R.ok(shopUserService.removeById(id));
    }


    /**
     * ??????????????????
     */
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    @GetMapping("/sms")
    @Inner(value = false)
    public R sendSms(@RequestParam("phone") String phone) {
        ShopUser shopUser = new ShopUser();
        shopUser.setPhone(phone);
        Wrapper<ShopUser> qw = new QueryWrapper<>(shopUser);
        try {
            sysConfigSmsService.sendVerifySms(phone);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return R.ok();

    }


    /**
     * ???????????????
     *
     * @param shopUser ????????????
     * @return R
     */
    @ApiOperation(value = "???????????????", notes = "???????????????")
    //@SysLog("??????????????????" )
    @PostMapping("/registered")
    @Inner(value = false)
    public R registered(@RequestBody ShopUser shopUser) {
//        SendMessageParam param=new SendMessageParam();
//        param.putData("code", RandomUtil.randomString(6));
//        param.setKey("registMsg");
//        param.setMethod(IMessageService.METHOD_SMS);
 //       this.messageService.send(param);

        String code = redisTemplate.opsForValue().get(shopUser.getPhoto()).toString();
        if (code == null || code.length() == 0) {
            return R.failed("???????????????");
        } else {
            if (code.equals(shopUser.getCodePhone().toLowerCase())) {
                Map<String, Object> map = shopUserService.loginVali(shopUser.getCode(), shopUser.getNickname(), shopUser.getAvatarUrl(), shopUser.getPhone());
                //TODO ??????????????????????????????????????????????????????????????????????????????????????????????????????
                ExamineTask examineTask = new ExamineTask();
                examineTask.setShopTel(shopUser.getPhone());
                Wrapper<ExamineTask> qw = new QueryWrapper<>(examineTask);
                List<ExamineTask> examineTaskList = examineTaskService.list(qw);
                ShopUser shopUser1 = new ShopUser();
                shopUser1.setPhone(shopUser.getPhone());
                ShopUser shopUser2 = shopUserService.getOne(new QueryWrapper<>(shopUser1));
                examineTaskList.forEach(a -> {
                    a.setUser(shopUser2.getId());
                    examineTaskService.updateById(a);
                });
                return R.ok(map);
            } else {
                return R.failed("??????????????????");
            }

        }

    }


    /**
     * ???????????????
     *
     * @return R
     */
    @ApiOperation(value = "???????????????", notes = "???????????????")
    //@SysLog("??????????????????" )
    @PostMapping("/login")
    @Inner(value = false)
    public R login(@RequestParam(required = false) String phone, @RequestParam(required = false) String code) {
        String code1 = redisTemplate.opsForValue().get(phone).toString();
        if (code == null || code.length() == 0) {
            return R.failed("???????????????");
        } else {
            if (code1.equals(code.toLowerCase())) {
                ShopUser shopUser1 = new ShopUser();
                if (StringUtils.isNotEmpty(phone)) {
                    ShopUser shopUser = new ShopUser();
                    shopUser.setPhone(phone);
                    Wrapper<ShopUser> qw = new QueryWrapper<>(shopUser);
                    shopUser1 = shopUserService.getOne(qw);
                    if (shopUser1 == null) {

                        return R.failed("??????????????????????????????");
                    } else {
                        return R.ok(shopUser1);
                    }
                } else {
                    return R.failed("?????????????????????");
                }
            } else {
                return R.failed("????????????????????????");
            }
        }

    }


    /**
     * ??????????????????
     */
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    @GetMapping("/select")
    @Inner(value = false)
    public R getById() {

        return R.ok(shopUserService.list());
    }



}
