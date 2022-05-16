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

package com.px.pa.modulars.sysConfigSms.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
//import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.px.pa.modulars.SysConfigSms.entity.SysConfigSms;
import com.px.pa.modulars.sysConfigSms.service.SysConfigSmsService;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * 
 *
 * @author lp
 * @date 2020-11-03 14:42:43
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/sysconfigsms" )
@Api(value = "sysconfigsms", tags = "管理")
public class SysConfigSmsController {

    private final  SysConfigSmsService sysConfigSmsService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param sysConfigSms 
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page" )
    @PreAuthorize("@pms.hasPermission('sysConfigSms_sysconfigsms_get')" )
    public R getSysConfigSmsPage(Page page, SysConfigSms sysConfigSms) {
        return R.ok(sysConfigSmsService.page(page, Wrappers.query(sysConfigSms)));
    }


    /**
     * 通过id查询
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}" )
    @PreAuthorize("@pms.hasPermission('sysConfigSms_sysconfigsms_get')" )
    public R getById(@PathVariable("id" ) Long id) {
        return R.ok(sysConfigSmsService.getById(id));
    }

    /**
     * 新增
     * @param sysConfigSms 
     * @return R
     */
    @ApiOperation(value = "新增", notes = "新增")
   // @SysLog("新增" )
    @PostMapping
    @PreAuthorize("@pms.hasPermission('sysConfigSms_sysconfigsms_add')" )
    public R save(@RequestBody SysConfigSms sysConfigSms) {
        return R.ok(sysConfigSmsService.save(sysConfigSms));
    }

    /**
     * 修改
     * @param sysConfigSms 
     * @return R
     */
    @ApiOperation(value = "修改", notes = "修改")
   // @SysLog("修改" )
    @PutMapping
    @PreAuthorize("@pms.hasPermission('sysConfigSms_sysconfigsms_edit')" )
    public R updateById(@RequestBody SysConfigSms sysConfigSms) {
        return R.ok(sysConfigSmsService.updateById(sysConfigSms));
    }

    /**
     * 通过id删除
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除", notes = "通过id删除")
   // @SysLog("通过id删除" )
    @DeleteMapping("/{id}" )
    @PreAuthorize("@pms.hasPermission('sysConfigSms_sysconfigsms_del')" )
    public R removeById(@PathVariable Long id) {
        return R.ok(sysConfigSmsService.removeById(id));
    }

}
