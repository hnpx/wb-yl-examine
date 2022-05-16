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

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.Inner;
import com.px.pa.modulars.examine.entity.ExamineLog;
import com.px.pa.modulars.examine.entity.ExamineTask;
import com.px.pa.modulars.examine.service.ExamineLogService;
import com.px.pa.modulars.examine.service.ExamineTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


/**
 * 
 *
 * @author lp
 * @date 2020-12-10 22:10:32
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/examinelog" )
@Api(value = "examinelog", tags = "管理")
public class ExamineLogApiController {

    private final  ExamineLogService examineLogService;
    private final ExamineTaskService examineTaskService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param examineLog 
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page" )
    @PreAuthorize("@pms.hasPermission('examineLog_examinelog_get')" )
    public R getExamineLogPage(Page page, ExamineLog examineLog) {
        return R.ok(examineLogService.page(page, Wrappers.query(examineLog)));
    }


    /**
     * 通过id查询
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}" )
    @PreAuthorize("@pms.hasPermission('examineLog_examinelog_get')" )
    public R getById(@PathVariable("id" ) Integer id) {
        return R.ok(examineLogService.getById(id));
    }

    /**
     * 新增
     * @param examineLog 
     * @return R
     */
    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('examineLog_examinelog_add')" )
    public R save(@RequestBody ExamineLog examineLog) {
        return R.ok(examineLogService.save(examineLog));
    }

    /**
     * 修改
     * @param examineLog 
     * @return R
     */
    @ApiOperation(value = "修改", notes = "修改")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('examineLog_examinelog_edit')" )
    public R updateById(@RequestBody ExamineLog examineLog) {
        return R.ok(examineLogService.updateById(examineLog));
    }

    /**
     * 通过id删除
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除", notes = "通过id删除")
    @DeleteMapping("/{id}" )
    @PreAuthorize("@pms.hasPermission('examineLog_examinelog_del')" )
    public R removeById(@PathVariable Integer id) {
        return R.ok(examineLogService.removeById(id));
    }



    /**
     *踏勘日志
     * @param examineLog
     * @return R
     */
    @ApiOperation(value = "踏勘日志", notes = "踏勘日志")
    @PostMapping("/add/now")
   @Inner(value = false)
    public R save1(@RequestBody ExamineLog examineLog) {
      ExamineTask examineTask = examineTaskService.getById(examineLog.getExamineTask());
        examineLog.setShop(examineTask.getUser());
        examineLog.setCreateTime(LocalDateTime.now());
        examineLog.setUpdateTime(LocalDateTime.now());
        return R.ok(examineLogService.save(examineLog));
    }

}
