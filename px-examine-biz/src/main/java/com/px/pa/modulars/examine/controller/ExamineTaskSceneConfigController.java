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
import com.px.pa.modulars.examine.entity.ExamineTaskSceneConfig;
import com.px.pa.modulars.examine.service.ExamineTaskSceneConfigService;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * 现场踏勘任务配置
 *
 * @author 吕郭飞
 * @date 2020-12-17 10:38:35
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/examinetasksceneconfig")
@Api(value = "examinetasksceneconfig", tags = "现场踏勘任务配置管理")
public class ExamineTaskSceneConfigController {

    private final ExamineTaskSceneConfigService examineTaskSceneConfigService;


    /**
     * 通过id查询现场踏勘任务配置
     *
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}")
//    @PreAuthorize("@pms.hasPermission('examine_examinetasksceneconfig_get')")
    public R getById(@PathVariable("id") Integer id) {
        return R.ok(examineTaskSceneConfigService.getById(id));
    }

    /**
     * 修改现场踏勘任务配置
     *
     * @param examineTaskSceneConfig 现场踏勘任务配置
     * @return R
     */
    @ApiOperation(value = "修改现场踏勘任务配置", notes = "修改现场踏勘任务配置")
    @PutMapping
    public R updateById(@RequestBody ExamineTaskSceneConfig examineTaskSceneConfig) {
        return R.ok(examineTaskSceneConfigService.updateById(examineTaskSceneConfig));
    }


}
