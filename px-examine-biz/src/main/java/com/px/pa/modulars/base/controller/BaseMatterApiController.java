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
import com.pig4cloud.pig.common.security.annotation.Inner;
import com.px.pa.modulars.base.entity.BaseMatter;
import com.px.pa.modulars.base.service.BaseMatterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 行政事务
 *
 * @author lp
 * @date 2020-11-26 14:31:19
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/basematter1" )
@Api(value = "basematter", tags = "行政事务管理")
public class BaseMatterApiController {

    private final  BaseMatterService baseMatterService;


    /**
     * 小程序事务列表
     * @param page 分页对象
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page/wx" )
    @Inner(value = false)
    public R getBaseMatterPage1(@RequestParam("page") Integer page,@RequestParam("pageSize") Integer pageSize) {
        Page page1 = new Page(page,pageSize);
        return R.ok(baseMatterService.page(page1));
    }


    /**
     * 事务详情
     * @return
     */
    @ApiOperation(value = "事务详情", notes = "事务详情")
    @GetMapping("/details" )
    @Inner(value = false)
    public R getBaseMatterPage1(@RequestParam("id") Integer id) {

        return R.ok(baseMatterService.getById(id));
    }



}
