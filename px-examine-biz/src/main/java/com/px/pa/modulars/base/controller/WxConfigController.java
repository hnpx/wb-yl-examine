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
import com.px.pa.modulars.base.entity.WxConfig;
import com.px.pa.modulars.base.service.WxConfigService;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * 微信配置
 *
 * @author lp
 * @date 2020-11-30 14:54:24
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/wxconfig" )
@Api(value = "wxconfig", tags = "微信配置管理")
public class WxConfigController {

    private final  WxConfigService wxConfigService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param wxConfig 微信配置
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page" )
    @PreAuthorize("@pms.hasPermission('generator_wxconfig_get')" )
    public R getWxConfigPage(Page page, WxConfig wxConfig) {
        return R.ok(wxConfigService.page(page, Wrappers.query(wxConfig)));
    }


    /**
     * 通过id查询微信配置
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}" )
    @PreAuthorize("@pms.hasPermission('generator_wxconfig_get')" )
    public R getById(@PathVariable("id" ) Long id) {
        return R.ok(wxConfigService.getById(id));
    }

    /**
     * 新增微信配置
     * @param wxConfig 微信配置
     * @return R
     */
    @ApiOperation(value = "新增微信配置", notes = "新增微信配置")
   // @SysLog("新增微信配置" )
    @PostMapping
    @PreAuthorize("@pms.hasPermission('generator_wxconfig_add')" )
    public R save(@RequestBody WxConfig wxConfig) {
        return R.ok(wxConfigService.save(wxConfig));
    }

    /**
     * 修改微信配置
     * @param wxConfig 微信配置
     * @return R
     */
    @ApiOperation(value = "修改微信配置", notes = "修改微信配置")
   // @SysLog("修改微信配置" )
    @PutMapping
    @PreAuthorize("@pms.hasPermission('generator_wxconfig_edit')" )
    public R updateById(@RequestBody WxConfig wxConfig) {
        return R.ok(wxConfigService.updateById(wxConfig));
    }

    /**
     * 通过id删除微信配置
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除微信配置", notes = "通过id删除微信配置")
  //  @SysLog("通过id删除微信配置" )
    @DeleteMapping("/{id}" )
    @PreAuthorize("@pms.hasPermission('generator_wxconfig_del')" )
    public R removeById(@PathVariable Long id) {
        return R.ok(wxConfigService.removeById(id));
    }

}
