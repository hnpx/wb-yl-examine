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
package com.px.pa.modulars.sysConfigSms.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.security.util.SendSmsService;
import com.px.msg.IMessageService;
import com.px.msg.MessageService;
import com.px.msg.vo.SendMessageParam;
import com.px.pa.modulars.SysConfigSms.entity.Aliyun;
import com.px.pa.modulars.SysConfigSms.entity.SysConfigSms;
import com.px.pa.modulars.SysConfigSms.mapper.SysConfigSmsMapper;
import com.px.pa.modulars.sysConfigSms.service.SysConfigSmsService;
import com.px.pa.modulars.upms.controller.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lp
 * @date 2020-11-03 14:42:43
 */
@Service
public class SysConfigSmsServiceImpl extends ServiceImpl<SysConfigSmsMapper, SysConfigSms> implements SysConfigSmsService {
    @Resource
    private RedisTemplate redisTemplate;
    @Autowired
    private MessageService messageService;

    /**
     * 发送短信
     *
     * @param phone
     * @return
     * @throws Exception
     */
    @Override
    public int sendVerifySms(String phone) throws Exception {
//        Map<String, String> result = new HashMap<>();
        Map<String, String> param = new HashMap<>();
        String code = RandomUtil.randomNumbers(6);
        if (StrUtil.isNotEmpty(phone)) {
//            result = sendSmsService.sendSms(phone, Aliyun.VERIFY_CODE, param);
            SendMessageParam msgParam = new SendMessageParam();
            msgParam.putData("code", code);
            msgParam.setKey("registMsg");
            msgParam.setMethod(IMessageService.METHOD_SMS);
            msgParam.addTo(phone);
            Map<String, Boolean> flags = this.messageService.send(msgParam);
            if (flags.get(IMessageService.METHOD_SMS)) {
                redisTemplate.opsForValue().set(phone, code,Duration.ofSeconds(120));
            }
        } else {
            throw new Exception("请输入手机号");
        }
        return 1;
    }


}

