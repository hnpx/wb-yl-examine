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

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.px.pa.constants.Constants;
import com.px.pa.modulars.examine.entity.ExamineTask;
import com.px.pa.modulars.examine.entity.ExamineVideoBin;
import com.px.pa.modulars.examine.mapper.ExamineVideoBinMapper;
import com.px.pa.modulars.examine.service.ExamineTaskService;
import com.px.pa.modulars.examine.service.ExamineVideoBinService;
import com.px.pa.modulars.examine.vo.ExamineVideoParam;
import com.px.pa.modulars.examine.vo.VideoInfo;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.DeleteMediaRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 踏勘视频回收记录
 *
 * @author 吕郭飞
 * @date 2020-12-16 11:51:15
 */
@Service
@Slf4j
public class ExamineVideoBinServiceImpl extends ServiceImpl<ExamineVideoBinMapper, ExamineVideoBin> implements ExamineVideoBinService {
    /**
     * 过期时间
     */
    @Value("${video-file.overdue-day}")
    private Integer overdueDay;
    @Autowired
    private VodClient vodClient;
    @Autowired
    private ExamineTaskService taskService;

    /**
     * 删除过期的文件
     */
    @Override
    public void delOverdueFiles() {
        int cc = this.overdueDay * 24 * 60 * 60 * 1000;
        long nowTime = System.currentTimeMillis();
        long fileTime = nowTime - cc;
        Date time = new Date(fileTime);
        ExamineVideoParam param = new ExamineVideoParam();
        param.setLastTime(time);
        param.setStatus(Constants.TASK_VIDEO_BIN_STATUS_DIS);
        List<ExamineVideoBin> vbs = super.baseMapper.queryByParam(param);
        if (vbs != null) {
            LocalDateTime now = LocalDateTime.now();
            vbs.forEach(v -> {
                v.setStatus(Constants.TASK_VIDEO_BIN_STATUS_DEL);
                v.setDelTime(now);
                DeleteMediaRequest req = new DeleteMediaRequest();
                req.setFileId(v.getFileId());
                try {
                    this.vodClient.DeleteMedia(req);
                } catch (TencentCloudSDKException e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
                super.updateById(v);
            });
        }
    }

    @Override
    public void disFile(Integer taskId, String fileId, Integer userId, String remark) {
        ExamineTask task = this.taskService.getById(taskId);
        if (task == null) {
            return;
        }
        String videoUrls = task.getVideoUrl();
        //如果符合标准继续处理
        if (StrUtil.isNotEmpty(videoUrls) && JSONUtil.isJsonArray(videoUrls)) {
            List<VideoInfo> videos = JSONUtil.toList(JSONUtil.parseArray(videoUrls), VideoInfo.class);
            //找到文件，删除文件
            for (VideoInfo v : videos) {
                if (v.getFileId().equals(fileId)) {
                    v.setEnable(false);
                    //存入删除对象
                    ExamineVideoBin vb = new ExamineVideoBin();
                    vb.setFileId(v.getFileId());
                    vb.setStatus(Constants.TASK_VIDEO_BIN_STATUS_DIS);
                    vb.setDelUser(userId);
                    vb.setDisTime(LocalDateTime.now());
                    vb.setDuration(v.getDuration());
                    vb.setStartTime(v.getStartTime());
                    vb.setEndTime(v.getEndTime());
                    vb.setTaskId(taskId);
                    vb.setVideoUrl(v.getUrl());
                    vb.setRemark(remark);
                    super.save(vb);
                    break;
                }
            }
            videoUrls = JSONUtil.toJsonStr(videos);
            task.setVideoUrl(videoUrls);
            this.taskService.updateById(task);
        }
    }

    @Override
    public void reduction(Integer taskId, String fileId) {
        ExamineTask task = this.taskService.getById(taskId);
        if (task == null) {
            return;
        }
        String videoUrls = task.getVideoUrl();
        //如果符合标准继续处理
        if (StrUtil.isNotEmpty(videoUrls) && JSONUtil.isJsonArray(videoUrls)) {
            List<VideoInfo> videos = JSONUtil.toList(JSONUtil.parseArray(videoUrls), VideoInfo.class);
            //找到文件，删除文件
            for (VideoInfo v : videos) {
                if (v.getFileId().equals(fileId)) {
                    v.setEnable(true);
                    break;
                }
            }
            videoUrls = JSONUtil.toJsonStr(videos);
            task.setVideoUrl(videoUrls);
            this.taskService.updateById(task);
        }
        ExamineVideoBin param = new ExamineVideoBin();
        param.setTaskId(taskId);
        param.setFileId(fileId);
        ExamineVideoBin vb = super.getOne(new QueryWrapper<>(param));
        vb.setStatus(Constants.TASK_VIDEO_BIN_STATUS_RED);
        super.updateById(vb);
    }


}
