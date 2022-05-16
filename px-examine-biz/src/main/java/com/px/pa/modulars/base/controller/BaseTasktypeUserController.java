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

package com.px.pa.modulars.base.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.px.pa.modulars.base.service.BaseTasktypeUserService;
import com.px.pa.modulars.base.entity.BaseTasktypeUser;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * 任务分类负责人
 *
 * @author lp
 * @date 2020-11-26 14:29:49
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/basetasktypeuser" )
@Api(value = "basetasktypeuser", tags = "任务分类负责人管理")
public class BaseTasktypeUserController {

    private final  BaseTasktypeUserService baseTasktypeUserService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param baseTasktypeUser 任务分类负责人
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page" )
    @PreAuthorize("@pms.hasPermission('base_basetasktypeuser_get')" )
    public R getBaseTasktypeUserPage(Page page, BaseTasktypeUser baseTasktypeUser) {
        return R.ok(baseTasktypeUserService.page(page, Wrappers.query(baseTasktypeUser)));
    }


    /**
     * 通过id查询任务分类负责人
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}" )
    @PreAuthorize("@pms.hasPermission('base_basetasktypeuser_get')" )
    public R getById(@PathVariable("id" ) Integer id) {
        return R.ok(baseTasktypeUserService.getById(id));
    }

    /**
     * 新增任务分类负责人
     * @param baseTasktypeUser 任务分类负责人
     * @return R
     */
    @ApiOperation(value = "新增任务分类负责人", notes = "新增任务分类负责人")
   // @SysLog("新增任务分类负责人" )
    @PostMapping
    @PreAuthorize("@pms.hasPermission('base_basetasktypeuser_add')" )
    public R save(@RequestBody BaseTasktypeUser baseTasktypeUser) {
        return R.ok(baseTasktypeUserService.save(baseTasktypeUser));
    }

    /**
     * 修改任务分类负责人
     * @param baseTasktypeUser 任务分类负责人
     * @return R
     */
    @ApiOperation(value = "修改任务分类负责人", notes = "修改任务分类负责人")
    //@SysLog("修改任务分类负责人" )
    @PutMapping
    @PreAuthorize("@pms.hasPermission('base_basetasktypeuser_edit')" )
    public R updateById(@RequestBody BaseTasktypeUser baseTasktypeUser) {
        return R.ok(baseTasktypeUserService.updateById(baseTasktypeUser));
    }

    /**
     * 通过id删除任务分类负责人
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除任务分类负责人", notes = "通过id删除任务分类负责人")
    //@SysLog("通过id删除任务分类负责人" )
    @DeleteMapping("/{id}" )
    @PreAuthorize("@pms.hasPermission('base_basetasktypeuser_del')" )
    public R removeById(@PathVariable Integer id) {
        return R.ok(baseTasktypeUserService.removeById(id));
    }

}
