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
 * ????????????
 *
 * @author lp
 * @date 2020-11-26 14:27:43
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/examinetask")
@Api(value = "examinetask", tags = "??????????????????")

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
     * ????????????
     *
     * @param page        ????????????
     * @param examineTask ????????????
     * @return
     */
    @ApiOperation(value = "????????????", notes = "????????????")
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
     * ??????id??????????????????
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "??????id??????", notes = "??????id??????")
    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('examine_examinetask_get')")
    public R getById(@PathVariable("id") Integer id) {
        return R.ok(examineTaskService.getById(id));
    }

    /**
     * ??????????????????
     *
     * @param examineTask ????????????
     * @return R
     */
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    // @SysLog("??????????????????" )
    @PostMapping
    @PreAuthorize("@pms.hasPermission('examine_examinetask_add')")
    public R save(@RequestBody ExamineTask examineTask) {

        //??????????????????????????????????????????????????????
        ShopUser shopUser = new ShopUser();
        shopUser.setPhone(examineTask.getShopTel());
        Wrapper<ShopUser> qw1 = new QueryWrapper<>(shopUser);
        ShopUser shopUser1 = shopUserService.getOne(qw1);
        if (shopUser1 == null) {
            /* return R.failed("?????????????????????????????????????????????");*/
            examineTask.setUser(null);
        } else {
            examineTask.setUser(shopUser1.getId());
        }

//?????????????????????????????????
        if (this.checkTask(examineTask.getUserName(), examineTask.getShopName(), examineTask.getIdCard(), examineTask.getTasktype())) {
            return R.failed("?????????????????????????????????????????????????????????");
        }

        Date date = new Date();
        String strDateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        //?????????????????????????????????
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


        //????????????
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
     * ????????????????????????????????????????????????
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
     * ????????????
     * id ??????id
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
     * ????????????????????????
     *
     * @param task
     * @return
     */
    private SendMessageParam createSmsMessageParam(ExamineTask task) {
        SendMessageParam param = new SendMessageParam();
        //????????????+???????????????
        param.setMethod(IMessageService.METHOD_SMS);
        // ??????????????????
        param.addTo(task.getShopTel());
        param.setKey("createRoom");
        String name = task.getUserName();
        param.putData("code", name, 20);
//        param.putData("code",task.get);
//        param.putData(task.getShopName());
        return param;
    }

    /**
     * ??????????????????
     *
     * @param examineTask ????????????
     * @return R
     */
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    //@SysLog("??????????????????" )
    @PutMapping
    @PreAuthorize("@pms.hasPermission('examine_examinetask_edit')")
    public R updateById(@RequestBody ExamineTask examineTask) {

        return R.ok(examineTaskService.updateById(examineTask));
    }

    /**
     * ??????id??????????????????
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "??????id??????????????????", notes = "??????id??????????????????")
    // @SysLog("??????id??????????????????" )
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('examine_examinetask_del')")
    public R removeById(@PathVariable Integer id) {
        return R.ok(examineTaskService.removeById(id));
    }


    /**
     * ????????????
     *
     * @param examineTask ????????????
     * @return R
     */
    @ApiOperation(value = "????????????????????????", notes = "????????????????????????")
    // @SysLog("??????????????????" )
    @PostMapping("/apply")
    public R save1(@RequestBody ExamineTask examineTask) {

        return R.ok(examineTaskService.save(examineTask));
    }


    /**
     * ????????????
     *
     * @return R
     */
    @ApiOperation(value = "????????????", notes = "????????????")
    @PostMapping("/opinion")
    public R save2(@RequestParam("examineTaskId") Integer examineTaskId, @RequestParam("opinion") String opinion) {
        SubmitExamineParam param = new SubmitExamineParam();
        param.setTaskId(examineTaskId);
        param.setRemark(opinion);
        boolean flag = this.examineTaskService.submit(param);
        return R.ok(flag);
    }


    /**
     * ?????????
     *
     * @return R
     */
    @ApiOperation(value = "?????????", notes = "?????????")
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
     * ???????????????
     *
     * @param param
     * @return
     */
    public R queryTodayTaskPool(@RequestBody TaskPoolParam param) {

        return R.ok();
    }


    /**
     * ????????????
     *
     * @param
     * @return
     */
    @ApiOperation(value = "????????????", notes = "????????????")
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
     * ????????????????????????
     *
     * @param
     * @return
     */
    @ApiOperation(value = "????????????????????????", notes = "????????????????????????")
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
    @ApiOperation("???????????????????????????")
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
    @ApiOperation("????????????")
    public Object assigned(@RequestParam("examineTask") Integer examineTask, @RequestParam("userId") Integer userId) {
        this.examineTaskService.assigned(examineTask, userId);
        return R.ok();

    }

    /**
     * ????????????
     *
     * @param id
     * @return
     */
    @GetMapping("/examine/video")
    @ApiOperation("????????????")
    public Object video(@RequestParam("id") Integer id) {
        ExamineTask examineTask = examineTaskService.getById(id);
        return R.ok(examineTask.getVideoUrl());
    }
    /**
     * ??????????????????
     *
     * @param id
     * @return R
     */
    @GetMapping("/send/result/{id}")
    @ApiOperation("??????????????????")
    public Object sendResult(@PathVariable("id") Integer id) {
        ExamineTask examineTask = examineTaskService.getById(id);
        if(examineTask != null){
            if(StringUtils.isNotEmpty(examineTask.getSystemId())){
                // ????????????
                examineTaskService.sysncData(examineTask);
                return R.ok();
            }else {
                return R.failed("??????????????????????????????????????????????????????????????????");

            }
        }else {
            return R.failed("?????????????????????");
        }

    }


    /**
     * ???????????????????????????
     *
     * @return
     */
     @Scheduled(cron = "0 */1 * * * ?")
    @GetMapping("/syncData")
    public void syncData() {

        List<ExamineTaskBySysTemVo> examineTaskBySysTemVoList = isMainService.getTaskListBystatus();
        //???????????????????????????
        examineTaskBySysTemVoList.forEach(examineTaskBySysTemVo -> {
            //????????????????????????
            Map<String, Object> map = examineTaskService.addTaskBySystem(examineTaskBySysTemVo);
        });
    }


    /**
     * ?????????????????????
     */
    @GetMapping("/syncData/result")
    public void syncDataResult() {
        //??????????????????????????????????????????????????????????????????
        List<ExamineTask> examineTaskList = examineTaskService.lambdaQuery()
                .ne(ExamineTask::getStatus, 1)
                .isNotNull(ExamineTask::getSystemId)
                .eq(ExamineTask::getState, 1)
                .list();
        examineTaskList.forEach(examineTask -> {
            // ????????????
            examineTaskService.sysncData(examineTask);
        });
    }



}
