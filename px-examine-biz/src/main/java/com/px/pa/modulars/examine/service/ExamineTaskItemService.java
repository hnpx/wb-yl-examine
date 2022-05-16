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

package com.px.pa.modulars.examine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.px.pa.modulars.examine.entity.ExamineImg;
import com.px.pa.modulars.examine.entity.ExamineLog;
import com.px.pa.modulars.examine.entity.ExamineTaskItem;
import com.px.pa.modulars.examine.vo.ExamineTaskBySysTemVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 任务审核
 *
 * @author lp
 * @date 2020-11-26 14:26:34
 */
public interface ExamineTaskItemService extends IService<ExamineTaskItem> {

   public int countByType(Integer tasktype, Integer shop,Integer examineTask);

   /**
    * 查询某个任务的所有审核项目
    * @param task
    * @return
    */
   public List<ExamineTaskItem> queryByTask(Integer task);

   /**
    * 查询某个任务的所有审核项目截图
    * @param task
    * @return
    */
   public List<ExamineImg> queryByTaskImg(Integer task);

   /**
    * 查询某个任务的所有视频记录
    * @param task
    * @return
    */
   public List<ExamineLog> queryByTaskLog(Integer task);



}
