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

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.Inner;
import com.px.pa.modulars.examine.entity.ExamineImg;
import com.px.pa.modulars.examine.entity.ExamineTask;
import com.px.pa.modulars.examine.entity.ExamineTaskItem;
import com.px.pa.modulars.examine.service.ExamineImgService;
import com.px.pa.modulars.examine.service.ExamineTaskItemService;
import com.px.pa.modulars.examine.service.ExamineTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 任务审核
 *
 * @author lp
 * @date 2020-11-26 14:26:34
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/examinetaskitem" )
@Api(value = "examinetaskitem", tags = "任务审核管理")
public class ExamineTaskItemApiController {

    private final  ExamineTaskItemService examineTaskItemService;
    private final ExamineTaskService examineTaskService;
    private final ExamineImgService examineImgService;


    /**
     * 微信小程序端  审核记录
     * @return R
     */
    @ApiOperation(value = "审核记录", notes = "审核记录")
    @GetMapping("/apply/record" )
    @Inner(value = false)
    public R getApplyRecord(@RequestParam("shop") Integer shop,@RequestParam("tasktype") Integer tasktype) {

        ExamineTaskItem examineTaskItem = new ExamineTaskItem();
        examineTaskItem.setShop(shop);
        examineTaskItem.setTasktype(tasktype);
        Wrapper<ExamineTaskItem> qw = new QueryWrapper<>(examineTaskItem);
        List<ExamineTaskItem> examineTaskItemList = examineTaskItemService.list(qw);
        examineTaskItemList.forEach(a->{
            ExamineImg examineImg = new ExamineImg();
            examineImg.setExamineTask(a.getTasktype());
            examineImg.setExamineTaskItem(a.getTasktypeItem());
            examineImg.setShop(a.getShop());
            Wrapper<ExamineImg> qw1 = new QueryWrapper<>(examineImg);
           ExamineImg examineImg1 =  examineImgService.getOne(qw1);
           a.setImg(examineImg1.getImgUrl());
        });
        return R.ok(examineTaskItemList);
    }


}
