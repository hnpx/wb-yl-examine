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

package com.px.pa.modulars.examine.controller;

import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.constant.Constants;
import com.pig4cloud.pig.common.core.util.R;
//import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.common.security.annotation.Inner;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.pig.common.security.util.mysql.IsMainService;
import com.px.msg.IMessageService;
import com.px.msg.MessageService;
import com.px.msg.vo.SendMessageParam;
import com.px.pa.constants.ExamineLogEnum;
import com.px.pa.constants.ExamineStatusEnum;
import com.px.pa.constants.TypeEnum;
import com.px.pa.modulars.base.entity.BaseTasktype;
import com.px.pa.modulars.base.entity.DataAnalysis;
import com.px.pa.modulars.base.service.BaseTasktypeService;
import com.px.pa.modulars.base.service.DataAnalysisService;
import com.px.pa.modulars.examine.entity.ExamineLog;
import com.px.pa.modulars.examine.entity.ExamineTask;
import com.px.pa.modulars.examine.entity.ExamineTaskItem;
import com.px.pa.modulars.examine.service.ExamineLogService;
import com.px.pa.modulars.examine.service.ExamineTaskItemService;
import com.px.pa.modulars.examine.service.ExamineTaskService;
import com.px.pa.modulars.examine.vo.*;
import com.px.pa.modulars.shop.entity.ShopUser;
import com.px.pa.modulars.shop.shopUser.service.ShopUserService;
import com.px.pa.modulars.upms.entity.SysUser;
import com.px.pa.modulars.upms.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.jws.Oneway;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.time.LocalDateTime;
import java.util.Date;


/**
 * 任务信息
 *
 * @author lp
 * @date 2020-11-26 14:27:43
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/examinetask")
@Api(value = "examinetask", tags = "任务信息管理")

public class ExamineTaskController  {

    private final ExamineTaskService examineTaskService;
    private final ExamineTaskItemService examineTaskItemService;
    private final BaseTasktypeService baseTasktypeService;
    private final DataAnalysisService dataAnalysisService;
    private final ShopUserService shopUserService;
    private final ExamineLogService examineLogService;
    private final SysUserService sysUserService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private IsMainService isMainService;

    private static ApplicationContext context;

    /**
     * 分页查询
     *
     * @param page        分页对象
     * @param examineTask 任务信息
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('examine_examinetask_get')")
    public R getExamineTaskPage(Page page, ExamineTask examineTask) {

        Page<ExamineTask> examineTaskPage = examineTaskService.page(page, new QueryWrapper<ExamineTask>()
                .like(examineTask.getShopName() != null, "shop_name", examineTask.getShopName())
                .like(examineTask.getUserName() != null, "user_name", examineTask.getUserName())
                .like(examineTask.getShopTel() != null, "shop_tel", examineTask.getShopTel())
                .eq(examineTask.getTasktype() != null, "tasktype", examineTask.getTasktype())
                .eq(examineTask.getStatus() != null, "status", examineTask.getStatus())
                .eq(examineTask.getContacts() != null, "contacts", examineTask.getContacts())
                .eq(examineTask.getState()!= null,"state",examineTask.getState())
                .isNotNull(examineTask.getState()!= null,"system_id")
                .orderByAsc(examineTask.getStatus() == 1, "apply_time").or()
                .orderByDesc(examineTask.getStatus() != 1, "update_time"));

        examineTaskPage.getRecords().forEach(a -> {
            BaseTasktype baseTasktype = baseTasktypeService.getById(a.getTasktype());
            try {
                a.setTasktypeName(baseTasktype.getName());
            } catch (Exception e) {
                a.setTasktypeName("");
            }
            try {
                SysUser sysUser = sysUserService.getById(a.getContacts());
                a.setContactsName(sysUser.getName());
            } catch (Exception e) {

                a.setContactsName("");
            }


        });
        return R.ok(examineTaskPage);
    }


    /**
     * 通过id查询任务信息
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('examine_examinetask_get')")
    public R getById(@PathVariable("id") Integer id) {
        return R.ok(examineTaskService.getById(id));
    }

    /**
     * 新增任务信息
     *
     * @param examineTask 任务信息
     * @return R
     */
    @ApiOperation(value = "新增任务信息", notes = "新增任务信息")
    // @SysLog("新增任务信息" )
    @PostMapping
    @PreAuthorize("@pms.hasPermission('examine_examinetask_add')")
    public R save(@RequestBody ExamineTask examineTask) {

        //通过手机号查找商户信息和商户进行关联
        ShopUser shopUser = new ShopUser();
        shopUser.setPhone(examineTask.getShopTel());
        Wrapper<ShopUser> qw1 = new QueryWrapper<>(shopUser);
        ShopUser shopUser1 = shopUserService.getOne(qw1);
        if (shopUser1 == null) {
            /* return R.failed("通过此手机号，未找到商户的信息");*/
            examineTask.setUser(null);
        } else {
            examineTask.setUser(shopUser1.getId());
        }

//如果重复，返回报错信息
        if (this.checkTask(examineTask.getUserName(), examineTask.getShopName(), examineTask.getIdCard(), examineTask.getTasktype())) {
            return R.failed("已经有同事录入了该任务，请不要重复录入");
        }

        Date date = new Date();
        String strDateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        //数据分析每日新增申请数
        DataAnalysis dataAnalysis = new DataAnalysis();
        dataAnalysis.setTime(sdf.format(date));
        Wrapper<DataAnalysis> qw = new QueryWrapper<>(dataAnalysis);
        try {
            DataAnalysis dataAnalysis1 = dataAnalysisService.getOne(qw);
            DataAnalysis dataAnalysis2 = new DataAnalysis();
            if (dataAnalysis1 == null) {
                dataAnalysis2.setTime(sdf.format(date));
                dataAnalysis2.setApplynum(1);
                dataAnalysisService.save(dataAnalysis2);
            } else {
                dataAnalysis2.setApplynum(dataAnalysis1.getApplynum() + 1);
                dataAnalysisService.update(dataAnalysis2, qw);
            }
        } catch (Exception e) {

        }


        //新增申请
        examineTask.setStatus(ExamineStatusEnum.EXAMINE_STATUS_ENUM_ONE.getValue());
        examineTask.setCreateTime(LocalDateTime.now());
        examineTask.setUpdateTime(LocalDateTime.now());
        examineTask.setType(TypeEnum.TYPE_ENUM_TWO.getValue());
        boolean flag = examineTaskService.save(examineTask);

        SendMessageParam msgParam = this.createSmsMessageParam(examineTask);
        ThreadUtil.execute(() -> {
            this.messageService.send(msgParam);
        });
        return R.ok(flag);
    }

    /**
     * 检查同一个人同时只有一个踏勘任务
     *
     * @param name
     * @param shopName
     * @param code
     * @return
     */
    private boolean checkTask(String name, String shopName, String code, Integer taskType) {
        ExamineTask param = new ExamineTask();
        param.setUserName(name);
        param.setShopName(shopName);
//        param.setShopTel(phone);
//        param.setIdCard(code);
//        param.setTasktype(taskType);
        try {
            ExamineTask task = this.examineTaskService.getOne(new QueryWrapper<>(param));
            if (task != null) {
                return true;
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    /**
     * 任务编号
     * id 任务id
     *
     * @return
     */
    @PostMapping("/number")
    public String getNumber(@RequestParam("id") Integer id) {

        String number = "";
        Date date = new Date();
        String strDateFormat = "yyyyMMdd";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        number = number + sdf.format(date) + "C";
        ExamineTask examineTask = examineTaskService.getById(id);
        String contacts = examineTask.getContacts().toString();
        if (contacts.length() == 1) {
            contacts = "00" + contacts;
        } else if (contacts.length() == 2) {
            contacts = "0" + contacts;
        } else if (contacts.length() == 3) {
            contacts = contacts;
        } else {
            contacts = contacts.substring(0, 3);
        }
        number = number + contacts + "T";

        String countByUser = String.valueOf(examineTaskService.countByUser(examineTask.getContacts()));

        if (countByUser.length() == 1) {
            countByUser = "00" + countByUser;
        } else if (countByUser.length() == 2) {
            countByUser = "0" + countByUser;

        } else {
            countByUser = countByUser.substring(0, 3);
        }

        number = number + countByUser;
        return number;
    }

    /**
     * 创建商户通知消息
     *
     * @param task
     * @return
     */
    private SendMessageParam createSmsMessageParam(ExamineTask task) {
        SendMessageParam param = new SendMessageParam();
        //通过短信+微信的方式
        param.setMethod(IMessageService.METHOD_SMS);
        // 查询任务分类
        param.addTo(task.getShopTel());
        param.setKey("createRoom");
        String name = task.getUserName();
        param.putData("code", name, 20);
//        param.putData("code",task.get);
//        param.putData(task.getShopName());
        return param;
    }

    /**
     * 修改任务信息
     *
     * @param examineTask 任务信息
     * @return R
     */
    @ApiOperation(value = "修改任务信息", notes = "修改任务信息")
    //@SysLog("修改任务信息" )
    @PutMapping
    @PreAuthorize("@pms.hasPermission('examine_examinetask_edit')")
    public R updateById(@RequestBody ExamineTask examineTask) {

        return R.ok(examineTaskService.updateById(examineTask));
    }

    /**
     * 通过id删除任务信息
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除任务信息", notes = "通过id删除任务信息")
    // @SysLog("通过id删除任务信息" )
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('examine_examinetask_del')")
    public R removeById(@PathVariable Integer id) {
        return R.ok(examineTaskService.removeById(id));
    }


    /**
     * 商铺申请
     *
     * @param examineTask 任务信息
     * @return R
     */
    @ApiOperation(value = "新增商铺申请信息", notes = "新增商铺申请信息")
    // @SysLog("新增任务信息" )
    @PostMapping("/apply")
    public R save1(@RequestBody ExamineTask examineTask) {

        return R.ok(examineTaskService.save(examineTask));
    }


    /**
     * 审核意见
     *
     * @return R
     */
    @ApiOperation(value = "审核意见", notes = "审核意见")
    @PostMapping("/opinion")
    public R save2(@RequestParam("examineTaskId") Integer examineTaskId, @RequestParam("opinion") String opinion) {
        SubmitExamineParam param = new SubmitExamineParam();
        param.setTaskId(examineTaskId);
        param.setRemark(opinion);
        boolean flag = this.examineTaskService.submit(param);
        return R.ok(flag);
    }


    /**
     * 任务池
     *
     * @return R
     */
    @ApiOperation(value = "任务池", notes = "任务池")
    @PostMapping("/pool")
    public R pool(Page page) {

        Page<Map<String, Object>> info = examineTaskService.getList(page);
        info.getRecords().forEach(a -> {
            BaseTasktype baseTasktype = baseTasktypeService.getById(Integer.parseInt(a.get("tasktype").toString()));
            a.put("name", baseTasktype.getName());
            a.put("notStart", examineTaskService.countBytime(Integer.parseInt(a.get("tasktype").toString()), 1));
            a.put("start", examineTaskService.countBytime(Integer.parseInt(a.get("tasktype").toString()), 2));
            a.put("pass", examineTaskService.countByStatus(Integer.parseInt(a.get("tasktype").toString()), ExamineStatusEnum.EXAMINE_STATUS_ENUM_TWO.getValue()));
            a.put("noPass", examineTaskService.countByStatus(Integer.parseInt(a.get("tasktype").toString()), ExamineStatusEnum.EXAMINE_STATUS_ENUM_THREE.getValue()));
        });

        return R.ok(info);
    }

    /**
     * 查询任务池
     *
     * @param param
     * @return
     */
    public R queryTodayTaskPool(@RequestBody TaskPoolParam param) {

        return R.ok();
    }


    /**
     * 踏勘地图
     *
     * @param
     * @return
     */
    @ApiOperation(value = "踏勘地图", notes = "踏勘地图")
    @PostMapping("/map")
    public R getMap() {
        List<ExamineTask> examineTaskList = examineTaskService.list(new QueryWrapper<ExamineTask>().eq("status", ExamineStatusEnum.EXAMINE_STATUS_ENUM_TWO
                .getValue()).or().eq("status", ExamineStatusEnum.EXAMINE_STATUS_ENUM_THREE.getValue()));
        List<MapVo> mapVoList = new ArrayList<>();
        examineTaskList.forEach(a -> {
            MapVo mapVo = new MapVo();
            mapVo.setId(a.getId());
            mapVo.setExamineLocation(a.getExamineLocation());
            mapVo.setShopName(a.getShopName());
            mapVoList.add(mapVo);
        });

        return R.ok(mapVoList);
    }


    /**
     * 踏勘地图店铺详情
     *
     * @param
     * @return
     */
    @ApiOperation(value = "踏勘地图店铺详情", notes = "踏勘地图店铺详情")
    @GetMapping("/map/detail")
    public R getMapDetail(@RequestParam("id") Integer id) {
        ExamineTask examineTask = examineTaskService.getById(id);
        ShopMapVo shopMapVo = new ShopMapVo();
        shopMapVo.setId(examineTask.getId());
        shopMapVo.setPhone(examineTask.getShopTel());
        shopMapVo.setShopName(examineTask.getShopName());
        shopMapVo.setStatus(examineTask.getStatus());
        shopMapVo.setShopAdress(examineTask.getShopAddress());

        return R.ok(shopMapVo);
    }


    @PutMapping("/read/waitTasks")
    @ApiOperation("查询我未踏勘的任务")
    public Object queryTasks(@RequestParam("page") Integer page, @RequestParam("pageSize") Integer pageSize) {

        Page page1 = new Page(page, pageSize);
        Integer userId = SecurityUtils.getUser().getId();
        ExamineTask examineTask = new ExamineTask();
        examineTask.setContacts(userId);
        examineTask.setStatus(ExamineStatusEnum.EXAMINE_STATUS_ENUM_ONE.getValue());
        Page<ExamineTask> examineTaskPage = examineTaskService.page(page1, new QueryWrapper<>(examineTask).orderByAsc("apply_time"));
        List<ExamineWaitVo> examineWaitVoList = new ArrayList<>();
        ExamineTaskWaitVo examineTaskWaitVo = new ExamineTaskWaitVo();
        examineTaskPage.getRecords().forEach(a -> {
            ExamineWaitVo examineWaitVo = new ExamineWaitVo();
            examineWaitVo.setApplyTime(a.getApplyTime());
            examineWaitVo.setPhone(a.getShopTel());
            examineWaitVo.setShopAddress(a.getShopAddress());
            examineWaitVo.setShopName(a.getShopName());
            examineWaitVo.setUserName(a.getUserName());
            examineWaitVo.setShop(a.getUser());
            examineWaitVo.setExamineTask(a.getId());
            examineWaitVo.setExamineType(a.getTasktype());
            examineWaitVoList.add(examineWaitVo);

        });
        int count = examineTaskService.count(new QueryWrapper<>(examineTask));
        examineTaskWaitVo.setCount(count);
        examineTaskWaitVo.setExamineWaitVoList(examineWaitVoList);
        return R.ok(examineTaskWaitVo);


    }


    @GetMapping("/examine/assigned")
    @ApiOperation("任务指派")
    public Object assigned(@RequestParam("examineTask") Integer examineTask, @RequestParam("userId") Integer userId) {
        this.examineTaskService.assigned(examineTask, userId);
        return R.ok();

    }

    /**
     * 视频详情
     *
     * @param id
     * @return
     */
    @GetMapping("/examine/video")
    @ApiOperation("任务指派")
    public Object video(@RequestParam("id") Integer id) {
        ExamineTask examineTask = examineTaskService.getById(id);
        return R.ok(examineTask.getVideoUrl());
    }
    /**
     * 发送探勘结果
     *
     * @param id
     * @return R
     */
    @GetMapping("/send/result/{id}")
    @ApiOperation("发送探勘结果")
    public Object sendResult(@PathVariable("id") Integer id) {
        ExamineTask examineTask = examineTaskService.getById(id);
        if(examineTask != null){
            if(StringUtils.isNotEmpty(examineTask.getSystemId())){
                // 同步数据
                examineTaskService.sysncData(examineTask);
                return R.ok();
            }else {
                return R.failed("不是政务系统推送踏勘任务不能进行发送踏勘结果");

            }
        }else {
            return R.failed("未找到踏勘任务");
        }

    }


    /**
     * 同步政务系统的数据
     *
     * @return
     */
     @Scheduled(cron = "0 */1 * * * ?")
    @GetMapping("/syncData")
    public void syncData() {

        List<ExamineTaskBySysTemVo> examineTaskBySysTemVoList = isMainService.getTaskListBystatus();
        //查询需要插入的数据
        examineTaskBySysTemVoList.forEach(examineTaskBySysTemVo -> {
            //插入数据台变状态
            Map<String, Object> map = examineTaskService.addTaskBySystem(examineTaskBySysTemVo);
        });
    }


    /**
     * 同步踏勘的结果
     */
    @GetMapping("/syncData/result")
    public void syncDataResult() {
        //查询所有是政务系统同步来的数据而且已完成踏勘
        List<ExamineTask> examineTaskList = examineTaskService.lambdaQuery()
                .ne(ExamineTask::getStatus, 1)
                .isNotNull(ExamineTask::getSystemId)
                .eq(ExamineTask::getState, 1)
                .list();
        examineTaskList.forEach(examineTask -> {
            // 同步数据
            examineTaskService.sysncData(examineTask);
        });
    }



}
