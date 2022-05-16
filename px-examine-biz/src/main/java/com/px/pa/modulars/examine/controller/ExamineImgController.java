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
import com.px.pa.modulars.examine.service.ExamineImgService;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


/**
 * 踏勘截图
 *
 * @author lp
 * @date 2020-12-08 10:39:51
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/examineimg" )
@Api(value = "examineimg", tags = "踏勘截图管理")
public class ExamineImgController {

    private final  ExamineImgService examineImgService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param examineImg 踏勘截图
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page" )
    @PreAuthorize("@pms.hasPermission('examineImg_examineimg_get')" )
    public R getExamineImgPage(Page page, ExamineImg examineImg) {
        return R.ok(examineImgService.page(page, Wrappers.query(examineImg)));
    }


    /**
     * 通过id查询踏勘截图
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}" )
    @PreAuthorize("@pms.hasPermission('examineImg_examineimg_get')" )
    public R getById(@PathVariable("id" ) Integer id) {
        return R.ok(examineImgService.getById(id));
    }

    /**
     * 新增踏勘截图
     * @param examineImg 踏勘截图
     * @return R
     */
    @ApiOperation(value = "新增踏勘截图", notes = "新增踏勘截图")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('examineImg_examineimg_add')" )
    public R save(@RequestBody ExamineImg examineImg) {
        return R.ok(examineImgService.save(examineImg));
    }

    /**
     * 修改踏勘截图
     * @param examineImg 踏勘截图
     * @return R
     */
    @ApiOperation(value = "修改踏勘截图", notes = "修改踏勘截图")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('examineImg_examineimg_edit')" )
    public R updateById(@RequestBody ExamineImg examineImg) {
        return R.ok(examineImgService.updateById(examineImg));
    }

    /**
     * 通过id删除踏勘截图
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除踏勘截图", notes = "通过id删除踏勘截图")
    @DeleteMapping("/{id}" )
    @PreAuthorize("@pms.hasPermission('examineImg_examineimg_del')" )
    public R removeById(@PathVariable Integer id) {
        return R.ok(examineImgService.removeById(id));
    }



    /**
     * 踏勘截图记录
     * @param examineImg 踏勘截图
     * @return R
     */
    @ApiOperation(value = "踏勘截图记录", notes = "踏勘截图记录")
    @PostMapping("/imgRecord")
    public R imgRecord(ExamineImg examineImg)
    {
       /* ExamineImg examineImg1 = new ExamineImg();
        examineImg1.setShop(examineImg.getShop());
        examineImg1.setExamineTask(examineImg.getExamineTask());
        examineImg1.setExamineTaskItem(null);
        Wrapper<ExamineImg> qw = new QueryWrapper<>(examineImg1);*/
       ExamineImg examineImg1 = examineImgService.getExamineImg(examineImg.getShop(),examineImg.getExamineTask());
        if(examineImg1 == null){
            ExamineImg examineImg2 = new ExamineImg();
            examineImg2.setShop(examineImg.getShop());
            examineImg2.setExamineTask(examineImg.getExamineTask());
            examineImg2.setCreateTime(LocalDateTime.now());
            examineImg2.setUpdateTime(LocalDateTime.now());
            examineImg2.setImgUrl(examineImg.getImgUrl());
            return R.ok(examineImgService.save(examineImg));
        }else {
            examineImg.setUpdateTime(LocalDateTime.now());
            examineImg.setId(examineImg1.getId());
            return R.ok(examineImgService.updateById(examineImg));
        }

    }

    /**
     * 查询踏勘截图记录
     * @return R
     */
    @ApiOperation(value = "踏勘截图记录", notes = "踏勘截图记录")
    @GetMapping("/select/imgRecord")
    public R imgRecordSelect(@RequestParam("shop") Integer shop,@RequestParam("examineTask") Integer examineTask)
    {
        ExamineImg examineImg1 = examineImgService.getExamineImg(shop,examineTask);
        return R.ok(examineImg1);

       // return R.ok(examineImgService.list(new QueryWrapper<ExamineImg>().eq("shop",shop).eq("examine_task",examineTask).eq("examine_task_item",null)));
    }


}
