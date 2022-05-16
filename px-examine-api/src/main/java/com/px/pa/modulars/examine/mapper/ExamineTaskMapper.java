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

package com.px.pa.modulars.examine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.px.pa.modulars.examine.entity.ExamineTask;
import com.px.pa.modulars.examine.vo.TaskQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 任务信息
 *
 * @author lp
 * @date 2020-11-26 14:27:43
 */
@Mapper
public interface ExamineTaskMapper extends BaseMapper<ExamineTask> {

    Page<Map<String, Object>> getList(@Param("page") Page page);

    int countBytime(@Param("tasktype") Integer tasktype, @Param("timeStatus") Integer timeStatus);

    int countByStatus(@Param("tasktype") Integer tasktype, @Param("status") Integer status);

    /**
     * 查询某一个时间范围内的所有任务
     *
     * @param param
     * @return
     */
    public List<ExamineTask> queryByParam(@Param("param")TaskQueryParam param,Page page);

    public List<Map<String,Object>> countTaskByStatus(@Param("param")TaskQueryParam param);

    public Page<Map<String,Object>> getPageList(@Param("page") Page page,@Param("tasktype") Integer tasktype,@Param("shop") Integer shop,@Param("status") Integer status);

    /**
     * 任务进行数
     * @return
     */
    public int countBytime1();

    /**
     * 今日审核数
     * @return
     */
    public int countApplyToday(@Param("type") Integer type);


    /**
     * 今日分配任务
     */

    public Page<Map<String,Object>> examineList(@Param("page") Page page);


    public int countByUser(@Param("user") Integer user);


    /**
     * 今日审核业务员
     * @return
     */
    public int countByUserToday();


    public int countTodayNum();
}
