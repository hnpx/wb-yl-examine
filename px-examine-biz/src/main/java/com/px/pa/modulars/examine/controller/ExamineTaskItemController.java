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

import cn.hutool.core.img.ImgUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.Inner;
import com.pig4cloud.pig.common.security.util.ExcelUtil.ExcelBaseUtil;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.px.fastfile.config.FastfileSiteConfig;
import com.px.fastfile.service.FastfileService;
import com.px.fastfile.vo.FastfileParam;
import com.px.pa.constants.Constants;
import com.px.pa.constants.ExamineItemStatusEnum;
import com.px.pa.constants.ExamineStatusEnum;
import com.px.pa.modulars.base.entity.BaseTasktype;
import com.px.pa.modulars.base.entity.BaseTasktypeItem;
import com.px.pa.modulars.base.service.BaseTasktypeItemService;
import com.px.pa.modulars.base.service.BaseTasktypeService;
import com.px.pa.modulars.examine.entity.ExamineImg;
import com.px.pa.modulars.examine.entity.ExamineLog;
import com.px.pa.modulars.examine.entity.ExamineTask;
import com.px.pa.modulars.examine.entity.ExamineTaskItem;
import com.px.pa.modulars.examine.service.ExamineImgService;
import com.px.pa.modulars.examine.service.ExamineLogService;
import com.px.pa.modulars.examine.service.ExamineTaskItemService;
import com.px.pa.modulars.examine.service.ExamineTaskService;
import com.px.pa.modulars.examine.vo.*;
import com.px.pa.modulars.shop.entity.ShopUser;
import com.px.pa.modulars.upms.entity.SysUserRole;
import com.px.pa.modulars.upms.service.SysUserRoleService;
import com.px.pa.modulars.upms.service.SysUserService;
import com.px.pa.modulars.vo.FastfileParamVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;


/**
 * 任务审核
 *
 * @author lp
 * @date 2020-11-26 14:26:34
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/examinetaskitem" )
@Api(value = "examinetaskitem", tags = "任务审核管理")
public class ExamineTaskItemController {

    private final  ExamineTaskItemService examineTaskItemService;
    private final ExamineTaskService examineTaskService;
    private final ExamineImgService examineImgService;
    private final SysUserService sysUserService;
    private final ExamineLogService examineLogService;
    private final BaseTasktypeService baseTasktypeService;
    private final BaseTasktypeItemService baseTasktypeItemService;
    private final SysUserRoleService sysUserRoleService;
    @Autowired
    private FastfileService fastfileService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param examineTaskItem 任务审核
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page" )
    @PreAuthorize("@pms.hasPermission('examine_examinetaskitem_get')" )
    public R getExamineTaskItemPage(Page page, ExamineTaskItem examineTaskItem) {
        return R.ok(examineTaskItemService.page(page, Wrappers.query(examineTaskItem)));
    }


    /**
     * 通过id查询任务审核
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}" )
    @PreAuthorize("@pms.hasPermission('examine_examinetaskitem_get')" )
    public R getById(@PathVariable("id" ) Integer id) {
        return R.ok(examineTaskItemService.getById(id));
    }

    /**
     * 新增任务审核
     * @param examineTaskItem 任务审核
     * @return R
     */
    @ApiOperation(value = "新增任务审核", notes = "新增任务审核")
    //@SysLog("新增任务审核" )
    @PostMapping("/addNow")
  //  @PreAuthorize("@pms.hasPermission('examine_examinetaskitem_add')" )
   // @Inner(value = false)
    public R save(ExamineTaskItem examineTaskItem) {
        ExamineTaskItem examineTaskItem1 = new ExamineTaskItem();
        examineTaskItem1.setTasktype(examineTaskItem.getTasktype());
        examineTaskItem1.setTasktypeItem(examineTaskItem.getTasktypeItem());
        examineTaskItem1.setShop(examineTaskItem.getShop());
        examineTaskItem1.setTotle(examineTaskItem.getTotle());
        examineTaskItem1.setExamineTask(examineTaskItem.getExamineTask());
        Wrapper<ExamineTaskItem> qw = new QueryWrapper<>(examineTaskItem1);
        if(examineTaskItemService.getOne(qw) == null){
            examineTaskItem1.setStatus(examineTaskItem.getStatus());
            examineTaskItem1.setCreateTime(LocalDateTime.now());
            examineTaskItem1.setUpdateTime(LocalDateTime.now());
            examineTaskItemService.save(examineTaskItem);
        }else {
            examineTaskItem1.setId(examineTaskItemService.getOne(qw).getId());
            examineTaskItem1.setUpdateTime(LocalDateTime.now());
            examineTaskItem1.setStatus(examineTaskItem.getStatus());
            examineTaskItemService.updateById(examineTaskItem1);
        }

        //图片上传
        ExamineImg examineImg = new ExamineImg();
        examineImg.setExamineTaskItem(examineTaskItem.getTasktypeItem());
        examineImg.setExamineTask(examineTaskItem.getExamineTask());
        examineImg.setShop(examineTaskItem.getShop());
        Wrapper<ExamineImg> qw1 = new QueryWrapper<>(examineImg);
        if(examineImgService.getOne(qw1) == null){
            ExamineImg examineImg1 = new ExamineImg();
            examineImg1.setExamineTaskItem(examineTaskItem.getTasktypeItem());
            examineImg1.setExamineTask(examineTaskItem.getExamineTask());
            examineImg1.setShop(examineTaskItem.getShop());
            examineImg1.setCreateTime(LocalDateTime.now());
            examineImg1.setUpdateTime(LocalDateTime.now());
            examineImg1.setImgUrl(examineTaskItem.getImg());
            examineImgService.save(examineImg1);
        }else {
            ExamineImg examineImg1 = new ExamineImg();
            examineImg1.setExamineTaskItem(examineTaskItem.getTasktypeItem());
            examineImg1.setExamineTask(examineTaskItem.getExamineTask());
            examineImg1.setShop(examineTaskItem.getShop());
            examineImg1.setId(examineImgService.getOne(qw1).getId());
            examineImg1.setUpdateTime(LocalDateTime.now());
            examineImg1.setImgUrl(examineTaskItem.getImg());
            examineImgService.updateById(examineImg1);
        }
        return R.ok();
    }


    /**
     * 审核详情
     * @param
     * @return R
     */
    @ApiOperation(value = "审核详情", notes = "审核详情")
    @PostMapping("/recording")
  /*  @Inner(value = false)*/
    public R recording(@RequestParam("examineTaskId") Integer examineTaskId) {

       ExamineTask examineTask = examineTaskService.getById(examineTaskId);
        List<ExamineItemRVo> examineItemRVoList = new ArrayList<>();
        List<ExamineTaskItem> examineTaskItemList =  examineTaskItemService.list(new QueryWrapper<ExamineTaskItem>()
                .eq("tasktype",examineTask.getTasktype()).eq("shop",examineTask.getUser()).eq("examine_task",examineTask.getId()).groupBy("totle"));

        List<BaseTasktypeItem> baseTasktypeItemList =   baseTasktypeItemService.list(new QueryWrapper<BaseTasktypeItem>().eq("tasktype",examineTask.getTasktype()).groupBy("totle").orderByAsc("id"));
        baseTasktypeItemList.forEach(a-> {
                    ExamineItemRVo examineItemRVo = new ExamineItemRVo();
                    examineItemRVo.setName(a.getTotle());
                    BaseTasktypeItem baseTasktypeItem = new BaseTasktypeItem();
                    baseTasktypeItem.setTotle(a.getTotle());
                    baseTasktypeItem.setTasktype(a.getTasktype());
                    Wrapper<BaseTasktypeItem> qw = new QueryWrapper<>(baseTasktypeItem);
                    List<BaseTasktypeItem> baseTasktypeItemList1 = baseTasktypeItemService.list(qw);
                    List<ExamineTaskItem> examineTaskItemList1 = new ArrayList<>();
                    baseTasktypeItemList1.forEach(b -> {
                        ExamineTaskItem examineTaskItem = new ExamineTaskItem();
                        examineTaskItem.setInfo(b.getInfo());
                        examineTaskItem.setTotle(b.getTotle());
                        examineTaskItem.setTasktype(b.getTasktype());
                        examineTaskItem.setExamineTask(examineTaskId);
                        examineTaskItem.setTasktypeItem(b.getId());
                        examineTaskItem.setShop(examineTask.getUser());

                        ExamineTaskItem examineTaskItem1 = new ExamineTaskItem();
                        examineTaskItem1.setExamineTask(examineTaskId);
                        examineTaskItem1.setShop(examineTask.getUser());
                        examineTaskItem1.setTasktypeItem(b.getId());
                        Wrapper<ExamineTaskItem> qw1 = new QueryWrapper<>(examineTaskItem1);
                        ExamineTaskItem examineTaskItem2 = examineTaskItemService.getOne(qw1);
                        if (examineTaskItem2 != null) {
                            examineTaskItem.setStatus(examineTaskItem2.getStatus());
                        }
                        ExamineImg examineImg = new ExamineImg();
                        examineImg.setShop(examineTask.getUser());
                        examineImg.setExamineTask(examineTaskId);
                        examineImg.setExamineTaskItem(b.getId());
                        Wrapper<ExamineImg> qw2 = new QueryWrapper<>(examineImg);
                      ExamineImg examineImg1 =  examineImgService.getOne(qw2);
                      if(examineImg1 != null){
                          examineTaskItem.setImg(examineImg1.getImgUrl());
                      }
                        examineTaskItemList1.add(examineTaskItem);
                        examineItemRVo.setExamineTaskItemList(examineTaskItemList1);

                    });

                  examineItemRVoList.add(examineItemRVo);

                });




       Map<String,Object> map = new HashMap<>();
       map.put("examineTaskItemList",examineItemRVoList);
       map.put("opinion",examineTask.getOpinion());
        ShopVo shopVo = new ShopVo();
        shopVo.setPhone(examineTask.getShopTel());
        shopVo.setShopAddress(examineTask.getShopAddress());
        shopVo.setShopLocation(examineTask.getExamineLocation());
        shopVo.setShopName(examineTask.getShopName());
        shopVo.setUserName(examineTask.getUserName());
        shopVo.setStatus(examineTask.getStatus());
       map.put("shopVo",shopVo);
        ExamineInfoVo examineInfoVo = new ExamineInfoVo();
        examineInfoVo.setActualApplyTime(examineTask.getActualApplyTime());
        examineInfoVo.setApplyTime(examineTask.getApplyTime());
        examineInfoVo.setCreateTime(examineTask.getCreateTime());
        examineInfoVo.setName(sysUserService.getById(examineTask.getContacts()).getName());
        examineInfoVo.setVideoStarttime(examineTask.getVideoStarttime());
        examineInfoVo.setVideoEndtime(examineTask.getVideoEndtime());
        map.put("examineInfoVo",examineInfoVo);
      /*  ExamineLog examineLog = new ExamineLog();
        examineLog.setShop(examineTask.getUser());
        examineLog.setExamineTask(examineTask.getId());
        Wrapper<ExamineLog> qw2 = new QueryWrapper<>(examineLog);*/
       /* List<ExamineLog> examineLogList =  examineLogService.list(new QueryWrapper<ExamineLog>().eq("shop",examineTask.getUser())
        .eq("examine_task",examineTask.getId()).orderByDesc("create_time"));*/

        List<ExamineLog> examineLogList =  examineLogService.getListByShopAndExamineTask(examineTask.getUser(),examineTask.getId());
        map.put("examineLogList",examineLogList);


     map.put("videoUrl",examineTask.getVideoUrl());

      int pass =  examineTaskItemService.count(new QueryWrapper<ExamineTaskItem>().eq("tasktype",examineTask.getTasktype())
              .eq("shop",examineTask.getUser())
              .eq("examine_task",examineTask.getId()).eq("status", ExamineItemStatusEnum.EXAMINE_STATUS_ENUM_ONE.getValue()));
        int noPass =  examineTaskItemService.count(new QueryWrapper<ExamineTaskItem>().eq("tasktype",examineTask.getTasktype())
                .eq("shop",examineTask.getUser())
                .eq("examine_task",examineTask.getId()).eq("status", ExamineItemStatusEnum.EXAMINE_STATUS_ENUM_TWO.getValue()));
        int change =  examineTaskItemService.count(new QueryWrapper<ExamineTaskItem>().eq("tasktype",examineTask.getTasktype())
                .eq("shop",examineTask.getUser())
                .eq("examine_task",examineTask.getId()).eq("status", ExamineItemStatusEnum.EXAMINE_STATUS_ENUM_THREE.getValue()));
        map.put("pass",pass);
        map.put("noPass",noPass);
        map.put("change",change);
        map.put("examineTask",examineTask.getId());
        map.put("shop",examineTask.getUser());
        map.put("number",examineTask.getNumber());
        //判断当前用户是否为超级管理员
        Integer userId = SecurityUtils.getUser().getId();
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setUserId(userId);
        Wrapper<SysUserRole> qw = new QueryWrapper<>(sysUserRole);
        SysUserRole sysUserRole1 = sysUserRoleService.getOne(qw);
        if(sysUserRole1.getRoleId().equals(Constants.ROLE_CJ)){
            map.put("status",Constants.ROLE_CJ);
        }else{
            map.put("status",Constants.ROLE_YW);
        }

        return R.ok(map);
    }

    /**
     * 修改任务审核
     * @param examineTaskItem 任务审核
     * @return R
     */
    @ApiOperation(value = "修改任务审核", notes = "修改任务审核")
    //@SysLog("修改任务审核" )
    @PutMapping
    @PreAuthorize("@pms.hasPermission('examine_examinetaskitem_edit')" )
    public R updateById(@RequestBody ExamineTaskItem examineTaskItem) {
        return R.ok(examineTaskItemService.updateById(examineTaskItem));
    }

    /**
     * 通过id删除任务审核
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除任务审核", notes = "通过id删除任务审核")
    //@SysLog("通过id删除任务审核" )
    @DeleteMapping("/{id}" )
    @PreAuthorize("@pms.hasPermission('examine_examinetaskitem_del')" )
    public R removeById(@PathVariable Integer id) {
        return R.ok(examineTaskItemService.removeById(id));
    }


    /**
     * 踏勘详情导出
     * @return R
     */
    @ApiOperation(value = "踏勘详情导出", notes = "踏勘详情导出")
    @GetMapping("/excel" )
   @Inner(value = false)
    public void excel(@RequestParam("shop") Integer shop,@RequestParam("examineTask") Integer examineTask,  HttpServletResponse response, HttpServletRequest request){

    ExamineTask examineTask1  =examineTaskService.getById(examineTask);
    List<BaseTasktypeItem> baseTasktypeItemList =   baseTasktypeItemService.list(new QueryWrapper<BaseTasktypeItem>().eq("tasktype",examineTask1.getTasktype()).groupBy("totle").orderByAsc("id"));
    int n = 0;
        List<ExamineItemExcelVo> examineItemRVoList = new ArrayList<>();
        for (BaseTasktypeItem baseTasktypeItem:baseTasktypeItemList) {
            BaseTasktypeItem baseTasktypeItem1 = new BaseTasktypeItem();
            baseTasktypeItem1.setTotle(baseTasktypeItem.getTotle());
            baseTasktypeItem1.setTasktype(baseTasktypeItem.getTasktype());
            Wrapper<BaseTasktypeItem> qw = new QueryWrapper<>(baseTasktypeItem1);
            List<BaseTasktypeItem> baseTasktypeItemList1 = baseTasktypeItemService.list(qw);
            for (BaseTasktypeItem baseTasktypeItem2:baseTasktypeItemList1) {
                n++;
                ExamineItemExcelVo examineItemExcelVo = new ExamineItemExcelVo();
                examineItemExcelVo.setTotle(baseTasktypeItem2.getTotle());
                examineItemExcelVo.setInfo(baseTasktypeItem2.getInfo());
                examineItemExcelVo.setNumber(n);

                ExamineTaskItem examineTaskItem = new ExamineTaskItem();
                examineTaskItem.setTasktype(baseTasktypeItem2.getTasktype());
                examineTaskItem.setShop(shop);
                examineTaskItem.setTasktypeItem(baseTasktypeItem2.getId());
                examineTaskItem.setExamineTask(examineTask);
                ExamineTaskItem examineTaskItem1 =   examineTaskItemService.getOne(new QueryWrapper<>(examineTaskItem));
                if (examineTaskItem1 != null){
                    if(examineTaskItem1.getStatus().equals(ExamineItemStatusEnum.EXAMINE_STATUS_ENUM_ONE.getValue())){
                        examineItemExcelVo.setOpinion(ExamineItemStatusEnum.EXAMINE_STATUS_ENUM_ONE.getDesc());
                    }if(examineTaskItem1.getStatus().equals(ExamineItemStatusEnum.EXAMINE_STATUS_ENUM_TWO.getValue())){
                        examineItemExcelVo.setOpinion(ExamineItemStatusEnum.EXAMINE_STATUS_ENUM_TWO.getDesc());
                    }if(examineTaskItem1.getStatus().equals(ExamineItemStatusEnum.EXAMINE_STATUS_ENUM_THREE.getValue())){
                        examineItemExcelVo.setOpinion(ExamineItemStatusEnum.EXAMINE_STATUS_ENUM_THREE.getDesc());
                    }
                }

                examineItemRVoList.add(examineItemExcelVo);

            }

        }
       BaseTasktype baseTasktype = baseTasktypeService.getById(examineTask1.getTasktype());
        String name ="《"+baseTasktype.getName()+"》" +"现场核查表";
        ExcelBaseUtil.exportExcel(examineItemRVoList,name,"踏勘详情",ExamineItemExcelVo.class,"踏勘详情.xls",response);

    }


    @ApiOperation(value = "使用base64上传图片文件，根据key去找对应的配置文件")
    @PostMapping(value = "/base64/{key}")
    public Object imageUp(@RequestBody FastfileParamVo param,
                          HttpServletRequest request,
                          HttpServletResponse response,
                          @PathVariable("key") String key) {
       ExamineTask examineTask =  examineTaskService.getById(param.getId());
        FastfileSiteConfig siteConfig = this.fastfileService.getSiteConfig(key);
        File file = this.fastfileService.baseToFile(siteConfig, param.getData());
        SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        ImgUtil.pressText(file,file,examineTask.getShopName()+ sdf1.format(date), Color.WHITE,new Font("宋体", Font.BOLD, 20),-20,300,0.4f);
        Map<String, Object> upResult = this.fastfileService.uploadFile(key, file);
        return R.ok(upResult);
    }

}
