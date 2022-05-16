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
import com.px.pa.modulars.base.entity.BaseTasktypeItem;
import com.px.pa.modulars.base.service.BaseTasktypeItemService;
import com.px.pa.modulars.examine.entity.ExamineImg;
import com.px.pa.modulars.examine.entity.ExamineTask;
import com.px.pa.modulars.examine.entity.ExamineTaskItem;
import com.px.pa.modulars.examine.service.ExamineImgService;
import com.px.pa.modulars.examine.service.ExamineTaskItemService;
import com.px.pa.modulars.examine.service.ExamineTaskService;
import com.px.pa.modulars.examine.vo.ExamineItemVo;
import com.px.pa.modulars.examine.vo.ShopVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.pig4cloud.pig.common.log.annotation.SysLog;


/**
 * 踏勘项目
 *
 * @author lp
 * @date 2020-11-26 14:30:25
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/basetasktypeitem" )
@Api(value = "basetasktypeitem", tags = "踏勘项目管理")
public class BaseTasktypeItemApiController {

    private final  BaseTasktypeItemService baseTasktypeItemService;
    private final ExamineTaskService examineTaskService;
    private final ExamineTaskItemService examineTaskItemService;
    private final ExamineImgService examineImgService;

    /**
     * 通过任务分类id查询审批项
     * @return
     */
    @ApiOperation(value = "通过任务分类id查询审批项", notes = "通过任务分类id查询审批项")
    @GetMapping("/page/byType" )
    @Inner(value = false)
    public R getBaseTasktypeItemPage1(@RequestParam("tasktype") Integer tasktype) {
        Map<String,Object> map = new HashMap<>();
        List<ExamineItemVo> examineItemVoList = new ArrayList<>();
        List<BaseTasktypeItem> baseTasktypeItemList = baseTasktypeItemService.list(new QueryWrapper<BaseTasktypeItem>().eq("tasktype",tasktype).groupBy("totle"));

        baseTasktypeItemList.forEach(a->{
            ExamineItemVo examineItemVo = new ExamineItemVo();
            examineItemVo.setName(a.getTotle());
            BaseTasktypeItem baseTasktypeItem = new BaseTasktypeItem();
            baseTasktypeItem.setTasktype(tasktype);
            baseTasktypeItem.setTotle(a.getTotle());
            Wrapper<BaseTasktypeItem> qw = new QueryWrapper<>(baseTasktypeItem);
            examineItemVo.setBaseTasktypeItemList(baseTasktypeItemService.list(qw));
            examineItemVoList.add(examineItemVo);

        });

        return R.ok(examineItemVoList);
    }



}
