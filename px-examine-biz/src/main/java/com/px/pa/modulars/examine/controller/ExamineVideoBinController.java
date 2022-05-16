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
import com.px.pa.constants.Constants;
import com.px.pa.modulars.examine.entity.ExamineVideoBin;
import com.px.pa.modulars.examine.service.ExamineVideoBinService;
import com.px.pa.modulars.examine.vo.ExamineVideoParam;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * 踏勘视频回收记录
 *
 * @author 吕郭飞
 * @date 2020-12-16 11:51:15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/examinevideobin")
@Api(value = "examinevideobin", tags = "踏勘视频回收记录管理")
public class ExamineVideoBinController {

    private final ExamineVideoBinService examineVideoBinService;

    /**
     * 分页查询
     *
     * @param page            分页对象
     * @param examineVideoBin 踏勘视频回收记录
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('examine_examinevideobin_get')")
    public R getExamineVideoBinPage(Page page, ExamineVideoBin examineVideoBin) {
        examineVideoBin.setStatus(Constants.TASK_VIDEO_BIN_STATUS_DIS);
        return R.ok(examineVideoBinService.page(page, Wrappers.query(examineVideoBin)));
    }


    /**
     * 通过id查询踏勘视频回收记录
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('examine_examinevideobin_get')")
    public R getById(@PathVariable("id") Integer id) {
        return R.ok(examineVideoBinService.getById(id));
    }

    /**
     * 新增踏勘视频回收记录
     *
     * @param examineVideoBin 踏勘视频回收记录
     * @return R
     */
    @ApiOperation(value = "新增踏勘视频回收记录", notes = "新增踏勘视频回收记录")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('examine_examinevideobin_add')")
    public R save(@RequestBody ExamineVideoBin examineVideoBin) {
        return R.ok(examineVideoBinService.save(examineVideoBin));
    }

    /**
     * 修改踏勘视频回收记录
     *
     * @param examineVideoBin 踏勘视频回收记录
     * @return R
     */
    @ApiOperation(value = "修改踏勘视频回收记录", notes = "修改踏勘视频回收记录")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('examine_examinevideobin_edit')")
    public R updateById(@RequestBody ExamineVideoBin examineVideoBin) {
        return R.ok(examineVideoBinService.updateById(examineVideoBin));
    }

    /**
     * 通过id删除踏勘视频回收记录
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除踏勘视频回收记录", notes = "通过id删除踏勘视频回收记录")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('examine_examinevideobin_del')")
    public R removeById(@PathVariable Integer id) {
        return R.ok(examineVideoBinService.removeById(id));
    }

    @ApiOperation(value = "还原任务", notes = "通过id删除踏勘视频回收记录")
    @PostMapping("/reduction")
    public R reduction(@RequestBody ExamineVideoParam param) {
        this.examineVideoBinService.reduction(param.getTaskId(), param.getFileId());
        return R.ok();
    }




}
