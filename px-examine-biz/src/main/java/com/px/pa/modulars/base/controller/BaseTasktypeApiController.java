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

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.Inner;
import com.px.pa.modulars.base.entity.BaseTasktype;
import com.px.pa.modulars.base.entity.BaseTasktypeItem;
import com.px.pa.modulars.base.service.BaseTasktypeItemService;
import com.px.pa.modulars.base.service.BaseTasktypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 任务分类
 *
 * @author lp
 * @date 2020-11-26 14:30:47
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/basetasktype" )
@Api(value = "basetasktype", tags = "任务分类管理")
public class BaseTasktypeApiController {

    private final  BaseTasktypeService baseTasktypeService;
    private final BaseTasktypeItemService baseTasktypeItemService;


    /**
     * 小程序端  任务分类详情
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/detail" )
    @Inner(value = false)
    public R getDetail(@RequestParam("id") Integer id) {
      BaseTasktype baseTasktype =  baseTasktypeService.getById(id);
        BaseTasktypeItem baseTasktypeItem = new BaseTasktypeItem();
        baseTasktypeItem.setTasktype(baseTasktype.getId());
        Wrapper<BaseTasktypeItem> wq = new QueryWrapper<>(baseTasktypeItem);
        baseTasktype.setBaseTasktypeItemList(baseTasktypeItemService.list(wq));
      return R.ok(baseTasktype);
    }

    /**
     * 查询任务列表
     */
    @ApiOperation(value = "查询任务列表", notes = "查询任务列表")
    @GetMapping("/select" )
    @Inner(value = false)
    public R getById() {
      List<BaseTasktype> baseTasktypeList =  baseTasktypeService.list();
        baseTasktypeList.forEach(a->{
            BaseTasktypeItem baseTasktypeItem = new BaseTasktypeItem();
            baseTasktypeItem.setTasktype(a.getId());
            Wrapper<BaseTasktypeItem> qw = new QueryWrapper<>(baseTasktypeItem);
            int n = baseTasktypeItemService.count(qw);
            a.setNum(n);
        });

        return R.ok(baseTasktypeList);
    }



    /**
     * 任务分类
     */
    @ApiOperation(value = "任务分类", notes = "任务分类")
    @GetMapping("/list" )
    @Inner(value = false)
    public R list() {
        List<BaseTasktype> baseTasktypeList =  baseTasktypeService.list();
        return R.ok(baseTasktypeList);
    }


}
