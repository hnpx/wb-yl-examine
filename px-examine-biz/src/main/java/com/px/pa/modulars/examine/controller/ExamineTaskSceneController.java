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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.constant.Constants;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.Inner;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.px.pa.modulars.examine.entity.ExamineTask;
import com.px.pa.modulars.examine.entity.ExamineTaskScene;
import com.px.pa.modulars.examine.service.ExamineTaskSceneService;
import com.px.pa.modulars.examine.service.ExamineTaskService;
import com.px.pa.modulars.examine.vo.SceneAssignParam;
import com.px.pa.modulars.examine.vo.SceneResultParam;
import com.px.pa.modulars.upms.entity.SysUser;
import com.px.pa.modulars.upms.service.SysUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


/**
 * 现场踏勘任务
 *
 * @author 吕郭飞
 * @date 2020-12-17 10:54:47
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/examinetaskscene")
@Api(value = "examinetaskscene", tags = "现场踏勘任务管理")
public class ExamineTaskSceneController {

    private final ExamineTaskSceneService examineTaskSceneService;

    private final ExamineTaskService taskService;
    private final SysUserService userService;
    private final ExamineTaskService examineTaskService;

    /**
     * 分页查询
     *
     * @param page             分页对象
     * @param examineTaskScene 现场踏勘任务
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('examine_examinetaskscene_get')")
    public R getExamineTaskScenePage(Page page, ExamineTaskScene examineTaskScene) {
        examineTaskScene.setEnable(Constants.ENABLE_TRUE);
        Integer status=examineTaskScene.getStatus();
        examineTaskScene.setStatus(null);
        QueryWrapper wrapper = Wrappers.query(examineTaskScene);
        wrapper.orderByDesc("create_time");
        if (status != null) {
            if (status == -1) {
                wrapper.isNull("contacts");

                wrapper.eq("status", com.px.pa.constants.Constants.TASK_STATUS_WAIT);
            } else if (status == -2) {
                wrapper.in("status", 2, 3);
            } else {
                wrapper.eq("status", status);
            }
        }
        Page<ExamineTaskScene> scenePage = examineTaskSceneService.page(page, wrapper);
        scenePage.getRecords().forEach(scene -> {
            ExamineTask task = this.taskService.getById(scene.getExamineTask());
            scene.setTask(task);
            if(scene.getContacts()!=null) {
                SysUser user = this.userService.getById(scene.getContacts());
                scene.setContactsName(user.getName());
            }
        });
        return R.ok(scenePage);
    }

    /**
     * 通过id查询现场踏勘任务
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('examine_examinetaskscene_get')")
    public R getById(@PathVariable("id") Integer id) {
        return R.ok(examineTaskSceneService.getById(id));
    }

    @ApiOperation(value = "通过任务id查询现场踏勘", notes = "通过id查询")
    @GetMapping("task/{id}")
  //  @PreAuthorize("@pms.hasPermission('examine_examinetaskscene_get')")
    @Inner(value = false)
    public R getByTaskId(@PathVariable("id") Integer id) {
        ExamineTaskScene param = new ExamineTaskScene();
        param.setExamineTask(id);
        ExamineTaskScene scene = this.examineTaskSceneService.getOne(Wrappers.query(param));
        return R.ok(scene);
    }

    /**
     * 修改现场踏勘任务
     *
     * @param param 现场踏勘任务
     * @return R
     */
    @ApiOperation(value = "指派现场踏勘", notes = "指派现场踏勘")
    @PostMapping("/assign")
    public R assign(@RequestBody SceneAssignParam param) {
        ExamineTaskScene scene = examineTaskSceneService.getById(param.getId());
        ExamineTask examineTask =  examineTaskService.getById(scene.getExamineTask());
       if(examineTask.getContacts().equals(param.getContacts())){
           return R.failed("现场踏勘任务不能再指派给此业务员");
       }
        this.examineTaskSceneService.assign(param);
        //远程踏勘给业务员新建一个踏勘的任务
        this.examineTaskSceneService.assignExamineTask(param);
        return R.ok();
    }

    @ApiOperation(value = "指派现场踏勘", notes = "指派现场踏勘")
    @PostMapping("/submit")
    public R submit(@RequestBody SceneResultParam param) {
        Integer userId = SecurityUtils.getUser().getId();
        this.examineTaskSceneService.submit(param, userId);
        return R.ok();
    }

    /**
     * 修改现场踏勘任务
     *
     * @param examineTaskScene 现场踏勘任务
     * @return R
     */
    @ApiOperation(value = "修改现场踏勘任务", notes = "修改现场踏勘任务")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('examine_examinetaskscene_edit')")
    public R updateById(@RequestBody ExamineTaskScene examineTaskScene) {
        return R.ok(examineTaskSceneService.updateById(examineTaskScene));
    }

    /**
     * 通过id删除现场踏勘任务
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除现场踏勘任务", notes = "通过id删除现场踏勘任务")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('examine_examinetaskscene_del')")
    public R removeById(@PathVariable Integer id) {
        ExamineTaskScene scene = this.examineTaskSceneService.getById(id);
        scene.setEnable(Constants.ENABLE_FALSE);
        scene.setUpdateBy(SecurityUtils.getUser().getId());
        scene.setUpdateTime(new Date());
        this.examineTaskSceneService.updateById(scene);
        return R.ok(true);
    }

}
