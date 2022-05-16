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
package com.px.pa.modulars.examine.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.px.msg.IMessageService;
import com.px.msg.MessageService;
import com.px.msg.vo.SendMessageParam;
import com.px.pa.constants.Constants;
import com.px.pa.constants.ExamineStatusEnum;
import com.px.pa.constants.TypeEnum;
import com.px.pa.modulars.examine.entity.ExamineTask;
import com.px.pa.modulars.examine.entity.ExamineTaskScene;
import com.px.pa.modulars.examine.entity.ExamineTaskSceneConfig;
import com.px.pa.modulars.examine.mapper.ExamineTaskSceneMapper;
import com.px.pa.modulars.examine.service.ExamineTaskSceneConfigService;
import com.px.pa.modulars.examine.service.ExamineTaskSceneService;
import com.px.pa.modulars.examine.service.ExamineTaskService;
import com.px.pa.modulars.examine.vo.SceneAssignParam;
import com.px.pa.modulars.examine.vo.SceneResultParam;
import com.px.pa.modulars.shop.entity.ShopUser;
import com.px.pa.modulars.shop.shopUser.service.ShopUserService;
import com.px.pa.modulars.upms.entity.SysUser;
import com.px.pa.modulars.upms.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

/**
 * 现场踏勘任务
 *
 * @author 吕郭飞
 * @date 2020-12-17 10:54:47
 */
@Service
@Slf4j
public class ExamineTaskSceneServiceImpl extends ServiceImpl<ExamineTaskSceneMapper, ExamineTaskScene> implements ExamineTaskSceneService {
    @Autowired
    private ExamineTaskSceneConfigService sceneConfigService;
    @Autowired
    private ExamineTaskService taskService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private ExamineTaskService examineTaskService;
    @Autowired
    private ShopUserService shopUserService;
    @Autowired
    private ExamineTaskSceneService examineTaskSceneService;

    @Override
    public boolean extract(ExamineTask task) {
        ExamineTaskSceneConfig config = this.sceneConfigService.getById(1);
        int ri = RandomUtil.randomInt(0, 100);
        if (ri <= config.getProbability()) {
            ExamineTaskScene scene = new ExamineTaskScene();
            scene.setCreateTime(LocalDateTime.now());
            scene.setExamineTask(task.getId());
            scene.setStatus(Constants.TASK_STATUS_WAIT);
            super.save(scene);
            return true;
        }
        return false;
    }

    @Override
    public void assign(SceneAssignParam param) {
        ExamineTaskScene scene = super.getById(param.getId());
        if (scene == null) {
            log.error("未找到数据：" + param.toString());
            return;
        }
        ExamineTask task = this.taskService.getById(scene.getExamineTask());
        //如果已经安排，则发送取消短信，然后发送新任务短信
        if (scene.getContacts() != null) {
            SysUser user = this.userService.getById(scene.getContacts());

            SendMessageParam msgParam = new SendMessageParam();
            msgParam.setMethod(IMessageService.METHOD_SMS);
            msgParam.setKey("cancelNotify");
            msgParam.putData("name", task.getTp().getName(), 20);
            msgParam.putData("time", DateUtil.format(scene.getApplyTime(), "yyyy-MM-dd HH:mm"));
            //通知管理員
            msgParam.addTo(user.getPhone());
            this.messageService.send(msgParam);
        }
        scene.setApplyTime(param.getApplyTime());
        scene.setContacts(param.getContacts());
        super.updateById(scene);
        // 向商户和业务员发送通知消息
        ThreadUtil.execute(() -> {
            SendMessageParam msgParam = new SendMessageParam();
            msgParam.setMethod(IMessageService.METHOD_SMS);
            msgParam.setKey("sceneNotifyUser");
            msgParam.putData("name", task.getTp().getName(), 20);
            msgParam.putData("time", DateUtil.format(scene.getApplyTime(), "yyyy-MM-dd HH:mm"));
            msgParam.putData("address", task.getShopAddress(), 20);
            //通知店铺
            msgParam.addTo(task.getShopTel());
            this.messageService.send(msgParam);
            SendMessageParam msgYwParam = new SendMessageParam();
            BeanUtil.copyProperties(msgParam, msgYwParam);
            msgYwParam.setTo(new ArrayList<>());
            SysUser user = this.userService.getById(scene.getContacts());
            //通知业务员
            if (user != null) {
                msgYwParam.addTo(user.getPhone());
                this.messageService.send(msgYwParam);
            }
        });
    }

    @Override
    public void assignExamineTask(SceneAssignParam param) {
        ExamineTaskScene scene = super.getById(param.getId());
        ExamineTask examineTask =  examineTaskService.getById(scene.getExamineTask());
        SysUser sysUser = userService.getById(examineTask.getContacts());
        ExamineTask examineTask1 = new ExamineTask();
        examineTask1.setType(TypeEnum.TYPE_ENUM_ONE.getValue());
        examineTask1.setShopAddress(examineTask.getShopAddress());
        examineTask1.setShopName(examineTask.getShopName());
        examineTask1.setShopTel(sysUser.getPhone());
        examineTask1.setUserName(sysUser.getUsername());
        examineTask1.setCreateTime(LocalDateTime.now());
        examineTask1.setStatus(ExamineStatusEnum.EXAMINE_STATUS_ENUM_ONE.getValue());
        examineTask1.setApplyTime(param.getApplyTime());
        examineTask1.setContacts(param.getContacts());
        examineTask1.setTasktype(examineTask.getTasktype());
        String number =  examineTaskService.getNumber(examineTask1);
        examineTask1.setNumber(number);


       ShopUser shopUser = shopUserService.getOne(new QueryWrapper<ShopUser>().eq("phone",sysUser.getPhone()));

       if(shopUser != null){
           examineTask1.setUser(shopUser.getId());
           examineTask1.setIdCard(shopUser.getIdCard());
       }

        examineTaskService.save(examineTask1);

    }

    @Override
    public void submit(SceneResultParam param, Integer userId) {
        ExamineTaskScene scene = super.getById(param.getId());
        if (scene == null) {
            log.error("未找到数据：" + param.toString());
            return;
        }
        scene.setImgUrls(param.getImgUrls());
        scene.setVideoUrls(param.getVideoUrls());
        scene.setStatus(param.getStatus());
        scene.setRemark(param.getRemark());
        scene.setUpdateBy(userId);
        scene.setUpdateTime(new Date());
        super.updateById(scene);
        //如果是不通过的话，修改踏勘状态
        if (scene.getStatus() == Constants.TASK_STATUS_ERROR) {
            ExamineTask task = this.taskService.getById(scene.getExamineTask());
            task.setStatus(scene.getStatus());
            task.setOpinion(task.getOpinion() + "\n" + task.getRemark());
            this.taskService.updateById(task);
        }
        //TODO 是否发送通知短信？正常现场踏勘应该不用再短信通知了
    }
}
