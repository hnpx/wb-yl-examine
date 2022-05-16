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

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.px.pa.modulars.examine.entity.ExamineTask;
import com.px.pa.modulars.examine.vo.*;
import com.px.pa.modulars.video.vo.LivePushParam;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * 任务信息
 *
 * @author lp
 * @date 2020-11-26 14:27:43
 */
public interface ExamineTaskService extends IService<ExamineTask> {

    /**
     * 开始某个任务的直播
     *
     * @param task
     */
    public void openRoom(ExamineTask task);

    /**
     * 指派踏勘任务给某个业务员
     *
     * @param taskId
     * @param userId
     */
    public void assigned(Integer taskId, Integer userId);


    /**
     * 指派踏勘任务给某个业务员进行现场踏勘
     *
     * @param taskId
     */
    public void assigned(Integer taskId);

    /**
     * 再次安排踏勘任务
     *
     * @param taskId
     * @param planTime
     */
    public void againPlan(Integer taskId, LocalDateTime planTime);

    /**
     * 管理员进入后轮训该请求，如果参数变为true，则表示商户已经进入，可以开始
     *
     * @param task
     */
    public boolean keepRoom(ExamineTask task);

    /**
     * 查询某个任务是否开始，管理员
     *
     * @param taskId
     * @return
     */
    public TaskRoomResult readRoom(Integer taskId);

    /**
     * 用户加入，等待对方进入房间开始视频
     *
     * @param userId 商户的用户ID
     * @param param  任务信息
     */
    public boolean intoRoom(Integer userId, IntoTaskRoomParam param);

    /**
     * 开始视频
     * @param taskId
     * @return
     */
    public boolean startVideo(Integer taskId,int flag);

    /**
     * @param task
     */
    public void leave(ExamineTask task);

    /**
     * 提交审核结果
     *
     * @param param
     * @return 如果需要现场踏勘则返回true
     */
    public boolean submit(SubmitExamineParam param);

    /**
     * 直播结束
     *
     * @param param
     */
    public void liveEnd(LivePushParam param);

    /**
     * 为业务员指派任务
     *
     * @param userId    踏勘业务员ID
     * @param startTime 时间范围：开始时间
     * @param endTime   时间范围：结束时间
     * @return 如果今日有任务，则指派
     */
    public ExamineTask assign(Integer userId, Date startTime, Date endTime);

    /**
     * 查询任务中心
     *
     * @param userId
     * @param startTime
     * @param endTime
     * @return
     */
    public TaskCenterResult readTaskCenter(Integer userId, Date startTime, Date endTime);

    /**
     * 查询商户的任务中心
     *
     * @param userId
     * @param startTime
     * @param endTime
     * @return
     */
    public ShopTaskCenterResult readTaskCenterByShop(Integer userId, Date startTime, Date endTime);

    /**
     * 查询任务列表
     *
     * @param param
     * @return
     */
    public Page<ExamineTask> queryTasks(TaskQueryParam param);

    /**
     * 任务池
     *
     * @return
     */
    public Page<Map<String, Object>> getList(Page page);

    public int countBytime(Integer tasktype, Integer timeStatus);

    public int countByStatus(Integer tasktype, Integer status);

    /**
     * 踏勘列表
     *
     * @param page
     * @param tasktype
     * @return
     */
    public Page<Map<String, Object>> getPageList(Page page, Integer tasktype, Integer shop, Integer status);

    /**
     * 任务进行数
     *
     * @return
     */
    public int countBytime1();


    /**
     * 今日审核数
     *
     * @return
     */
    public int countApplyToday(Integer type);


    /**
     * 今日分配任务
     */

    public Page<Map<String, Object>> examineList(@Param("page") Page page);

    /**
     * 查询审核员今日审核数
     *
     * @param user
     * @return
     */
    public int countByUser(@Param("user") Integer user);

    /**
     * 发送审核结果通知
     *
     * @param task
     */
    public void sendApplyMessage(ExamineTask task,boolean falg);

    /**
     * 今日审核业务员
     *
     * @return
     */
    public int countByUserToday();

    /**
     * 今日是否还有未分配的任务
     *
     * @return
     */

    public int countTodayNum();

    public String getNumber(ExamineTask examineTask);


    /**
     * 取政务系统的数据添加任务
     */
    public Map<String,Object> addTaskBySystem(ExamineTaskBySysTemVo examineTaskBySysTemVo);

    /**
     *
     * 踏勘结束同步前置机的数据
     */
    public void sysncData(ExamineTask examineTask);

}
