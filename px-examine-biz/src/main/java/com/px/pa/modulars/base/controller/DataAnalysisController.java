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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.Inner;
import com.px.pa.constants.Constants;
import com.px.pa.constants.ExamineStatusEnum;
import com.px.pa.constants.TypeStatusEnum;
import com.px.pa.modulars.base.entity.BaseTasktype;
import com.px.pa.modulars.base.entity.DataAnalysis;
import com.px.pa.modulars.base.service.BaseTasktypeService;
import com.px.pa.modulars.base.service.DataAnalysisService;
import com.px.pa.modulars.examine.entity.ExamineTask;
import com.px.pa.modulars.examine.service.ExamineTaskService;
import com.px.pa.modulars.examine.vo.HomeDataVo;
import com.px.pa.modulars.examine.vo.PercentageVo;
import com.px.pa.modulars.upms.entity.SysUser;
import com.px.pa.modulars.upms.entity.SysUserRole;
import com.px.pa.modulars.upms.mapper.SysUserMapper;
import com.px.pa.modulars.upms.service.SysUserRoleService;
import com.px.pa.modulars.upms.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;


/**
 * 
 *
 * @author lp
 * @date 2020-12-09 15:46:50
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/dataanalysis" )
@Api(value = "dataanalysis", tags = "??????")
public class DataAnalysisController {

    @Autowired
    private  DataAnalysisService dataAnalysisService;
    @Autowired
    private BaseTasktypeService baseTasktypeService;
    @Autowired
    private ExamineTaskService examineTaskService;
    @Autowired
    private SysUserService sysUserService;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysUserRoleService sysUserRoleService;


    /**
     * ????????????
     * @param page ????????????
     * @param dataAnalysis 
     * @return
     */
    @ApiOperation(value = "????????????", notes = "????????????")
    @GetMapping("/page" )
    @PreAuthorize("@pms.hasPermission('dataAnalysis_dataanalysis_get')" )
    public R getDataAnalysisPage(Page page, DataAnalysis dataAnalysis) {
        return R.ok(dataAnalysisService.page(page, Wrappers.query(dataAnalysis)));
    }


    /**
     * ??????id??????
     * @param id id
     * @return R
     */
    @ApiOperation(value = "??????id??????", notes = "??????id??????")
    @GetMapping("/{id}" )
    @PreAuthorize("@pms.hasPermission('dataAnalysis_dataanalysis_get')" )
    public R getById(@PathVariable("id" ) Integer id) {
        return R.ok(dataAnalysisService.getById(id));
    }

    /**
     * ??????
     * @param dataAnalysis 
     * @return R
     */
    @ApiOperation(value = "??????", notes = "??????")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('dataAnalysis_dataanalysis_add')" )
    public R save(@RequestBody DataAnalysis dataAnalysis) {
        return R.ok(dataAnalysisService.save(dataAnalysis));
    }

    /**
     * ??????
     * @param dataAnalysis 
     * @return R
     */
    @ApiOperation(value = "??????", notes = "??????")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('dataAnalysis_dataanalysis_edit')" )
    public R updateById(@RequestBody DataAnalysis dataAnalysis) {
        return R.ok(dataAnalysisService.updateById(dataAnalysis));
    }

    /**
     * ??????id??????
     * @param id id
     * @return R
     */
    @ApiOperation(value = "??????id??????", notes = "??????id??????")
    @DeleteMapping("/{id}" )
    @PreAuthorize("@pms.hasPermission('dataAnalysis_dataanalysis_del')" )
    public R removeById(@PathVariable Integer id) {
        return R.ok(dataAnalysisService.removeById(id));
    }





    /**
     * ???????????????????????????
     * ???????????????????????????
     *????????????
     * @return R
     */

   @Scheduled(cron = "0 0 0 * * ?")
    public R cron()
    {
        Date date = new Date();
        String strDateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        //?????????????????????????????????
        DataAnalysis dataAnalysis = new DataAnalysis();
        dataAnalysis.setTime(sdf.format(date));
        dataAnalysis.setApplynum(0);
        dataAnalysis.setAuditnum(0);
        dataAnalysisService.save(dataAnalysis);
        return R.ok();
    }


    /**
     * ???????????????????????????
     * ???????????????????????????
     *???????????????
     * @return R
     */
    @ApiOperation(value = "???????????????", notes = "???????????????")
    @PostMapping("/page/getList")
    //@Inner(value = false)
    public R getList()
    {
        Page page = new Page(1,7);
        Page<Map<String,Object>> dataAnalysisPage =  dataAnalysisService.getList(page);
        return R.ok(dataAnalysisPage);
    }


    /**
     *??????????????????????????????
     * @return R
     */
    @ApiOperation(value = "??????????????????????????????", notes = "??????????????????????????????")
    @PostMapping("/percentage")
   // @Inner(value = false)
    public R percentage()
    {
        int countSum =  examineTaskService.count();
      List<ExamineTask> examineTaskList = examineTaskService.list(new QueryWrapper<ExamineTask>().groupBy("tasktype"));
      List<PercentageVo> percentageVoList = new ArrayList<>();
        examineTaskList.forEach(a->{
         BaseTasktype baseTasktype =   baseTasktypeService.getById(a.getTasktype());
            PercentageVo percentageVo = new PercentageVo();
            try {
                percentageVo.setTasktype(baseTasktype.getName());
            }catch (Exception e){
                percentageVo.setTasktype("");
            }

           int count =  examineTaskService.count(new QueryWrapper<ExamineTask>().eq("tasktype",a.getTasktype()));
            percentageVo.setCount(count);
            int pass =  examineTaskService.count(new QueryWrapper<ExamineTask>().eq("tasktype",a.getTasktype()).eq("status", ExamineStatusEnum.EXAMINE_STATUS_ENUM_TWO.getValue()));
            int noPass =  examineTaskService.count(new QueryWrapper<ExamineTask>().eq("tasktype",a.getTasktype()).eq("status", ExamineStatusEnum.EXAMINE_STATUS_ENUM_THREE.getValue()));
            int apply =  examineTaskService.count(new QueryWrapper<ExamineTask>().eq("tasktype",a.getTasktype()).eq("status", ExamineStatusEnum.EXAMINE_STATUS_ENUM_ONE.getValue()));
            percentageVo.setPass(pass);
            percentageVo.setNoPass(noPass);
            percentageVo.setApply(apply);
            percentageVo.setCountSum(countSum);
            percentageVoList.add(percentageVo);
        });

        return R.ok(percentageVoList);
    }


    /**
     *???????????????
     *
     * ??????????????????
     *
     * ??????????????????
     *
     * ??????????????????
     * @return R
     */
   // @ApiOperation(value = "??????????????????????????????", notes = "??????????????????????????????")
    @PostMapping("/examineNum")
  //  @Inner(value = false)
    public R examineNum()
    {
      List<String> dataList =  getDaysBetwwen(15);
        List<PercentageVo> percentageVoList = new ArrayList<>();
        for (int i=0;i< dataList.size();i++) {

            PercentageVo percentageVo = new PercentageVo();
            int count =  examineTaskService.count();
            percentageVo.setCount(count);
            int pass =  examineTaskService.count(new QueryWrapper<ExamineTask>().eq("status", ExamineStatusEnum.EXAMINE_STATUS_ENUM_TWO.getValue()).like("apply_time",dataList.get(i)));
            int noPass =  examineTaskService.count(new QueryWrapper<ExamineTask>().eq("status", ExamineStatusEnum.EXAMINE_STATUS_ENUM_THREE.getValue()).like("apply_time",dataList.get(i)));
            int apply =  examineTaskService.count(new QueryWrapper<ExamineTask>().eq("status", ExamineStatusEnum.EXAMINE_STATUS_ENUM_ONE.getValue()).like("apply_time",dataList.get(i)));
            percentageVo.setPass(pass);
            percentageVo.setNoPass(noPass);
            percentageVo.setApply(apply);
            percentageVo.setDate(dataList.get(i));
            percentageVoList.add(percentageVo);
        }


        return R.ok(percentageVoList);
    }



    /**
     * ????????????????????????
     * @return R
     */
    @ApiOperation(value = "????????????????????????", notes = "????????????????????????")
    @PostMapping("/apply/notice")
   // @Inner(value = false)
    public R apply()
    {
    List<ExamineTask> examineTaskList =  examineTaskService.list(new QueryWrapper<ExamineTask>().eq("status",ExamineStatusEnum.EXAMINE_STATUS_ENUM_ONE.getValue()));
        examineTaskList.forEach(a->{
         BaseTasktype baseTasktype = baseTasktypeService.getById(a.getTasktype());
         try{
             a.setTasktypeName(baseTasktype.getName());
         }catch (Exception e){
             a.setTasktypeName("");
         }

        });
    return R.ok(examineTaskList);
    }



    /**
     *???????????????????????????
     * @return R
     */
    @ApiOperation(value = "???????????????????????????", notes = "???????????????????????????")
    @PostMapping("/percentage/sum")
   // @Inner(value = false)
    public R percentageSum()
    {
        int countSum =  examineTaskService.count();
        PercentageVo percentageVo = new PercentageVo();
        int pass =  examineTaskService.count(new QueryWrapper<ExamineTask>().eq("status", ExamineStatusEnum.EXAMINE_STATUS_ENUM_TWO.getValue()));
        int noPass =  examineTaskService.count(new QueryWrapper<ExamineTask>().eq("status", ExamineStatusEnum.EXAMINE_STATUS_ENUM_THREE.getValue()));
        int apply =  examineTaskService.count(new QueryWrapper<ExamineTask>().eq("status", ExamineStatusEnum.EXAMINE_STATUS_ENUM_ONE.getValue()));
        percentageVo.setPass(pass);
        percentageVo.setNoPass(noPass);
        percentageVo.setApply(apply);
        percentageVo.setCountSum(countSum);

       /* List<ExamineTask> examineTaskList = examineTaskService.list(new QueryWrapper<ExamineTask>().groupBy("tasktype"));
        List<PercentageVo> percentageVoList = new ArrayList<>();
        int countSum =  examineTaskService.count();

        examineTaskList.forEach(a->{
            BaseTasktype baseTasktype =   baseTasktypeService.getById(a.getTasktype());
            PercentageVo percentageVo = new PercentageVo();
            percentageVo.setTasktype(baseTasktype.getName());
            int count =  examineTaskService.count(new QueryWrapper<ExamineTask>().eq("tasktype",a.getTasktype()));
            percentageVo.setCount(count);
            percentageVo.setCountSum(countSum);
            percentageVoList.add(percentageVo);
        });
*/
        return R.ok(percentageVo);
    }


    /**
     * ????????????????????????
     * @return R
     */
    // @ApiOperation(value = "??????????????????????????????", notes = "??????????????????????????????")
    @PostMapping("/apply/percentage")
   // @Inner(value = false)
    public R applyPercentage()
    {

        List<PercentageVo> percentageVoList = new ArrayList<>();
        PercentageVo percentageVo = new PercentageVo();
        int count =  examineTaskService.count();
        int countSum =  examineTaskService.count();
        percentageVo.setCount(count);
        int pass =  examineTaskService.count(new QueryWrapper<ExamineTask>().eq("status", ExamineStatusEnum.EXAMINE_STATUS_ENUM_TWO.getValue()));
        int noPass =  examineTaskService.count(new QueryWrapper<ExamineTask>().eq("status", ExamineStatusEnum.EXAMINE_STATUS_ENUM_THREE.getValue()));
        int apply =  examineTaskService.count(new QueryWrapper<ExamineTask>().eq("status", ExamineStatusEnum.EXAMINE_STATUS_ENUM_ONE.getValue()).gt("actual_apply_time",LocalDateTime.now()));
        int run  = examineTaskService.countBytime1();
        percentageVo.setPass(pass);
        percentageVo.setNoPass(noPass);
        percentageVo.setApply(apply);
        percentageVo.setRun(run);
        percentageVo.setCountSum(countSum);
        percentageVoList.add(percentageVo);

        return R.ok(percentageVoList);
    }


    /**
     * ????????????
     * @return R
     */
     @ApiOperation(value = "????????????", notes = "????????????")
    @PostMapping("/home/data")
   // @Inner(value = false)
    public R homeData(){
         HomeDataVo homeDataVo = new HomeDataVo();
         //???????????????
         int examineNum = examineTaskService.count();
         homeDataVo.setExamineNum(examineNum);
         //????????????
         int waitApply = examineTaskService.count(new QueryWrapper<ExamineTask>().eq("status",ExamineStatusEnum.EXAMINE_STATUS_ENUM_ONE.getValue()));
         homeDataVo.setWaitApply(waitApply);
         //?????????
         homeDataVo.setAlreadyApply(examineNum - waitApply);
         //????????????
         int noPass = examineTaskService.count(new QueryWrapper<ExamineTask>().eq("status",ExamineStatusEnum.EXAMINE_STATUS_ENUM_THREE.getValue()));
         homeDataVo.setNoPass(noPass);
         //??????????????????
        // int salesman = sysUserService.count(new QueryWrapper<SysUser>().eq("del_flag","0")); //0??????1.??????
         SysUserRole sysUserRole = new SysUserRole();
         sysUserRole.setRoleId(Constants.ROLE_YW);
         int salesman = sysUserRoleService.count(new QueryWrapper<>(sysUserRole));
         homeDataVo.setSalesman(salesman);
         //??????????????????
       //  int salesmanToday =  dataAnalysisService.countByMan();
         int salesmanToday = examineTaskService.countByUserToday();
         homeDataVo.setSalesmanToday(salesmanToday);
         //???????????????
         int alreadyApplyToday =  examineTaskService.countApplyToday(TypeStatusEnum.TYPE_STATUS_ENUM_ONE.getValue());
         homeDataVo.setAlreadyApplyToday(alreadyApplyToday);
         //??????????????????
         int noPassToday = examineTaskService.countApplyToday(TypeStatusEnum.TYPE_STATUS_ENUM_TWO.getValue());
         homeDataVo.setNoPassToday(noPassToday);
         return R.ok(homeDataVo);
    }

    /**
     *??????????????????
     * @return R
     */
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    @PostMapping("/today/examine")
   // @Inner(value = false)
    public R examineToday(@RequestParam("page") Integer page,@RequestParam("pageSize") Integer pageSize){
        Page page1 = new Page(page,pageSize);
      Page<Map<String,Object>> pageList =  examineTaskService.examineList(page1);
      if(pageList.getRecords().size()>0){
          pageList.getRecords().forEach(a->{
             try {
                 BaseTasktype baseTasktype =   baseTasktypeService.getById(Integer.parseInt(a.get("tasktype").toString()));
                 a.put("tasktypeName",baseTasktype.getName());
             }catch (Exception e){
                 a.put("tasktypeName","");
             }
              try{
                  SysUser sysUser =  sysUserService.getById(Integer.parseInt(a.get("contacts").toString()));
                  a.put("contactsName",sysUser.getName());
              }catch (Exception e){
                  a.put("contactsName","");
              }
          });
      }
      return R.ok(pageList);

    }




    public  List<String> getDaysBetwwen(int days){ //??????????????????
        List<String> dayss = new ArrayList<>();
        Calendar start = Calendar.getInstance();
        start.setTime(getDateAdd(days));
        Long startTIme = start.getTimeInMillis();
        Calendar end = Calendar.getInstance();
        end.setTime(new Date());
        Long endTime = end.getTimeInMillis();
        Long oneDay = 1000 * 60 * 60 * 24L;
        Long time = startTIme;
        while (time <= endTime) {
            Date d = new Date(time);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            System.out.println(df.format(d));
            dayss.add(df.format(d));
            time += oneDay;
        }
        return dayss;
    }

    private  Date getDateAdd(int days){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -days);
        return c.getTime();
    }


}
