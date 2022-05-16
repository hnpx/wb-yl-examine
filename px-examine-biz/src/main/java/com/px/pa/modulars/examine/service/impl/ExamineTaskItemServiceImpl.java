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
package com.px.pa.modulars.examine.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.util.mysql.IsMainService;
import com.px.msg.IMessageService;
import com.px.msg.MessageService;
import com.px.msg.vo.SendMessageParam;
import com.px.pa.constants.ExamineStatusEnum;
import com.px.pa.constants.TypeEnum;
import com.px.pa.modulars.base.entity.DataAnalysis;
import com.px.pa.modulars.base.service.BaseTasktypeService;
import com.px.pa.modulars.base.service.DataAnalysisService;
import com.px.pa.modulars.examine.entity.ExamineImg;
import com.px.pa.modulars.examine.entity.ExamineLog;
import com.px.pa.modulars.examine.entity.ExamineTask;
import com.px.pa.modulars.examine.entity.ExamineTaskItem;
import com.px.pa.modulars.examine.mapper.ExamineTaskItemMapper;
import com.px.pa.modulars.examine.service.ExamineImgService;
import com.px.pa.modulars.examine.service.ExamineLogService;
import com.px.pa.modulars.examine.service.ExamineTaskItemService;
import com.px.pa.modulars.examine.service.ExamineTaskService;
import com.px.pa.modulars.examine.vo.ExamineTaskBySysTemVo;
import com.px.pa.modulars.shop.entity.ShopUser;
import com.px.pa.modulars.shop.shopUser.service.ShopUserService;
import com.px.pa.modulars.upms.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务审核
 *
 * @author lp
 * @date 2020-11-26 14:26:34
 */
@Service
public class ExamineTaskItemServiceImpl extends ServiceImpl<ExamineTaskItemMapper, ExamineTaskItem> implements ExamineTaskItemService {

    @Resource
    private ExamineTaskItemMapper examineTaskItemMapper;
    @Resource
    private ExamineImgService examineImgService;
    @Resource
    private ExamineLogService examineLogService;
    @Resource
    private ExamineTaskItemService examineTaskItemService;


    @Override
    public int countByType(Integer tasktype, Integer shop,Integer examineTask) {
        return examineTaskItemMapper.countByType(tasktype, shop,examineTask);
    }

    @Override
    public List<ExamineTaskItem> queryByTask(Integer task) {
      /*  Map<String,Object> param=new HashMap<>();
        param.put("examine_task",task);
        List<ExamineTaskItem> taskItems = super.listByMap(param);*/
        ExamineTaskItem examineTaskItem = new ExamineTaskItem();
        examineTaskItem.setExamineTask(task);
        List<ExamineTaskItem> taskItems =  examineTaskItemService.list(new QueryWrapper<>(examineTaskItem));
        return taskItems;
    }

    @Override
    public List<ExamineImg> queryByTaskImg(Integer task) {
        ExamineImg examineImg1 = new ExamineImg();
        examineImg1.setExamineTask(task);
        List<ExamineImg> examineImgList =  examineImgService.list(new QueryWrapper<>(examineImg1));
        return examineImgList;
    }

    @Override
    public List<ExamineLog> queryByTaskLog(Integer task) {
        ExamineLog examineLog = new ExamineLog();
        examineLog.setExamineTask(task);
        List<ExamineLog> examineLogList =  examineLogService.list(new QueryWrapper<>(examineLog));
        return examineLogList;
    }


}
