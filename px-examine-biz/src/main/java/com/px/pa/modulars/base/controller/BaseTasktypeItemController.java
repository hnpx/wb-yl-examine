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
//import com.pig4cloud.pig.common.log.annotation.SysLog;
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
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ????????????
 *
 * @author lp
 * @date 2020-11-26 14:30:25
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/basetasktypeitem" )
@Api(value = "basetasktypeitem", tags = "??????????????????")
public class BaseTasktypeItemController {

    private final  BaseTasktypeItemService baseTasktypeItemService;
    private final ExamineTaskService examineTaskService;
    private final ExamineTaskItemService examineTaskItemService;
    private final ExamineImgService examineImgService;

    /**
     * ????????????
     * @param page ????????????
     * @param baseTasktypeItem ????????????
     * @return
     */
    @ApiOperation(value = "????????????", notes = "????????????")
    @GetMapping("/page" )
    //@PreAuthorize("@pms.hasPermission('base_basetasktypeitem_get')" )
   // @Inner(value = false)
    public R getBaseTasktypeItemPage(Page page, BaseTasktypeItem baseTasktypeItem) {
        return R.ok(baseTasktypeItemService.page(page, Wrappers.query(baseTasktypeItem)));
    }


    /**
     * ??????id??????????????????
     * @param id id
     * @return R
     */
    @ApiOperation(value = "??????id??????", notes = "??????id??????")
    @GetMapping("/{id}" )
   // @PreAuthorize("@pms.hasPermission('base_basetasktypeitem_get')" )
    //@Inner(value = false)
    public R getById(@PathVariable("id" ) Integer id) {
        return R.ok(baseTasktypeItemService.getById(id));
    }

    /**
     * ??????????????????
     * @param baseTasktypeItem ????????????
     * @return R
     */
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    //@SysLog("??????????????????" )
    @PostMapping
    //@PreAuthorize("@pms.hasPermission('base_basetasktypeitem_add')" )
   // @Inner(value = false)
    public R save(@RequestBody BaseTasktypeItem baseTasktypeItem) {
        return R.ok(baseTasktypeItemService.save(baseTasktypeItem));
    }

    /**
     * ??????????????????
     * @param baseTasktypeItem ????????????
     * @return R
     */
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
   // @SysLog("??????????????????" )
    @PutMapping
    //@PreAuthorize("@pms.hasPermission('base_basetasktypeitem_edit')" )
    //@Inner(value = false)
    public R updateById(@RequestBody BaseTasktypeItem baseTasktypeItem) {
        return R.ok(baseTasktypeItemService.updateById(baseTasktypeItem));
    }

    /**
     * ??????id??????????????????
     * @param id id
     * @return R
     */
    @ApiOperation(value = "??????id??????????????????", notes = "??????id??????????????????")
   // @SysLog("??????id??????????????????" )
    @DeleteMapping("/{id}" )
    //@PreAuthorize("@pms.hasPermission('base_basetasktypeitem_del')" )
   // @Inner(value = false)
    public R removeById(@PathVariable Integer id) {
        return R.ok(baseTasktypeItemService.removeById(id));
    }



    /**
     * ??????????????????id???????????????
     * @return
     */
    @ApiOperation(value = "??????????????????id???????????????", notes = "??????????????????id???????????????")
    @GetMapping("/page/byType" )
 //   @Inner(value = false)
    public R getBaseTasktypeItemPage1(@RequestParam("tasktype") Integer tasktype,@RequestParam("examineTask") Integer examineTask,@RequestParam("shop") Integer shop) {
        Map<String,Object> map = new HashMap<>();
      List<ExamineItemVo> examineItemVoList = new ArrayList<>();
        List<BaseTasktypeItem> baseTasktypeItemList = baseTasktypeItemService.list(new QueryWrapper<BaseTasktypeItem>().eq("tasktype",tasktype).groupBy("totle"));

        try{
           baseTasktypeItemList.forEach(a->{

               ExamineItemVo examineItemVo = new ExamineItemVo();
               examineItemVo.setName(a.getTotle());
               BaseTasktypeItem baseTasktypeItem = new BaseTasktypeItem();
               baseTasktypeItem.setTasktype(tasktype);
               baseTasktypeItem.setTotle(a.getTotle());
               Wrapper<BaseTasktypeItem> qw = new QueryWrapper<>(baseTasktypeItem);
               List<BaseTasktypeItem> baseTasktypeItemList1 = baseTasktypeItemService.list(qw);
               baseTasktypeItemList1.forEach(b->{

                   ExamineTaskItem examineTaskItem = new ExamineTaskItem();
                   examineTaskItem.setShop(shop);
                   examineTaskItem.setExamineTask(examineTask);
                   examineTaskItem.setTasktype(tasktype);
                   examineTaskItem.setTasktypeItem(b.getId());
                   Wrapper<ExamineTaskItem> qw1 = new QueryWrapper<>(examineTaskItem);
                   ExamineTaskItem examineTaskItem1 = examineTaskItemService.getOne(qw1);
                   if(examineTaskItem1 != null){

                       b.setStatus(examineTaskItem1.getStatus());
                   }
                   ExamineImg examineImg = new ExamineImg();
                   examineImg.setShop(shop);
                   examineImg.setExamineTaskItem(b.getId());
                   examineImg.setExamineTask(examineTask);
                   Wrapper<ExamineImg> qw2 = new QueryWrapper<>(examineImg);
                   ExamineImg examineImg1 =  examineImgService.getOne(qw2);
                   if(examineImg1 != null){
                       b.setImg(examineImg1.getImgUrl());
                   }


                   examineItemVo.setBaseTasktypeItemList(baseTasktypeItemList1);
               });

               examineItemVoList.add(examineItemVo);
           });
       }catch (Exception e){

       }


        ExamineTask examineTask1 = examineTaskService.getById(examineTask);
        ShopVo shopVo = new ShopVo();
        shopVo.setShopName(examineTask1.getShopName());
        shopVo.setShopLocation(examineTask1.getExamineLocation());
        shopVo.setPhone(examineTask1.getShopTel());
        shopVo.setUserName(examineTask1.getUserName());
        shopVo.setShopAddress(examineTask1.getShopAddress());
        map.put("shopVo",shopVo);
        map.put("examineItemVoList",examineItemVoList);
        return R.ok(map);
    }



}
