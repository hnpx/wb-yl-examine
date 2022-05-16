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
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.core.exception.IllegalParameterException;
import com.pig4cloud.pig.common.core.support.cache.RedisHelper;
import com.pig4cloud.pig.common.core.support.page.PageUtil;
import com.pig4cloud.pig.common.security.annotation.Inner;
import com.pig4cloud.pig.common.security.util.mysql.IsMainService;
import com.px.msg.IMessageService;
import com.px.msg.MessageService;
import com.px.msg.vo.SendMessageParam;
import com.px.pa.constants.Constants;
import com.px.pa.constants.ExamineLogEnum;
import com.px.pa.constants.ExamineStatusEnum;
import com.px.pa.constants.TypeEnum;
import com.px.pa.modulars.base.entity.BaseTasktype;
import com.px.pa.modulars.base.entity.DataAnalysis;
import com.px.pa.modulars.base.service.BaseTasktypeService;
import com.px.pa.modulars.base.service.DataAnalysisService;
import com.px.pa.modulars.examine.entity.ExamineImg;
import com.px.pa.modulars.examine.entity.ExamineLog;
import com.px.pa.modulars.examine.entity.ExamineTask;
import com.px.pa.modulars.examine.entity.ExamineTaskItem;
import com.px.pa.modulars.examine.mapper.ExamineTaskMapper;
import com.px.pa.modulars.examine.service.ExamineImgService;
import com.px.pa.modulars.examine.service.ExamineLogService;
import com.px.pa.modulars.examine.service.ExamineTaskItemService;
import com.px.pa.modulars.examine.service.ExamineTaskService;
import com.px.pa.modulars.examine.vo.*;
import com.px.pa.modulars.shop.entity.ShopUser;
import com.px.pa.modulars.shop.shopUser.service.ShopUserService;
import com.px.pa.modulars.upms.entity.SysUser;
import com.px.pa.modulars.upms.service.SysUserService;
import com.px.pa.modulars.video.vo.LivePushParam;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.trtc.v20190722.TrtcClient;
import com.tencentcloudapi.trtc.v20190722.models.*;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.MediaInfo;
import com.tencentcloudapi.vod.v20180717.models.SearchMediaRequest;
import com.tencentcloudapi.vod.v20180717.models.SearchMediaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 任务信息
 *
 * @author lp
 * @date 2020-11-26 14:27:43
 */
@Service
public class ExamineTaskServiceImpl extends ServiceImpl<ExamineTaskMapper, ExamineTask> implements ExamineTaskService {
    @Autowired
    private ShopUserService shopUserService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private RedisHelper redisHelper;
    @Resource
    private ExamineTaskMapper examineTaskMapper;
    @Autowired
    private BaseTasktypeService tasktypeService;
    @Autowired
    private ExamineTaskItemService examineTaskItemService;

    @Autowired
    private ExamineLogService examineLogService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ExamineTaskSceneServiceImpl sceneService;
    @Autowired
    private DataAnalysisService dataAnalysisService;
    @Autowired
    private TrtcClient trtcClient;

    @Autowired
    private VodClient client;
    @Autowired
    private ExamineImgService examineImgService;


    @Resource
    private  ExamineTaskService examineTaskService;
    @Resource
    private  BaseTasktypeService baseTasktypeService;
    @Autowired
    private IsMainService isMainService;

    @Override
    public void openRoom(ExamineTask task) {
        //发送通知消息
        // 判断商户是否在系统中，如果在的话，直接给商户推送消息
        if (task.getUser() == null) {
            SendMessageParam msgParam = this.createSmsRemindRoomMessageParam(task);
            ThreadUtil.execute(() -> {
                this.messageService.send(msgParam);
            });
        }
        // 判断任务是否由商户发起，如果是商户发起，则给商户推送订阅消息
//        else if (task.getUser().equals(task.getCreateBy())) {
        else {
            ThreadUtil.execute(() -> {
                SendMessageParam msgParam = this.createWxMessageParam(task);
                Map<String, Boolean> sendFlag = this.messageService.send(msgParam);
                //如果订阅消息推送失败，则向商户推送短信
//                if (!sendFlag.get(IMessageService.METHOD_WX)) {
                SendMessageParam smsMsgParam = this.createSmsRemindRoomMessageParam(task);
                this.messageService.send(smsMsgParam);
//                }
            });
        }
        String videoUrls = task.getVideoUrl();
        int i = 0;
        if (videoUrls != null) {
            JSONArray ja = JSONUtil.parseArray(videoUrls);
            i = ja.size();
        }

        Object obj = this.redisHelper.get(Constants.ROOM_ADMIN_FLAG + task.getId());
        if (obj != null && obj.equals("true")) {

        } else {
            //记录管理员进入的时间
            this.redisHelper.set(Constants.ROOM_ADMIN_FLAG + task.getId(), LocalDateTime.now(), Constants.ROOM_ADMIN_FLAG_OUTTIME);
            this.redisHelper.set(Constants.USER_ONLINE_INFO + task.getContacts().toString(), new Boolean(true), Constants.USER_ONLINE_INFO_TIMEOUT);
        }

        task.setVideoStarttime(LocalDateTime.now());
        super.updateById(task);
        //写入当前直播房间，暂时使用任务ID
      /*  if (task.getVideoStarttime() == null) {

        }*/

    }

    @Override
    public void assigned(Integer taskId, Integer userId) {
        ExamineTask task = this.getById(taskId);
        Integer oldTaskContacts = task.getContacts();
        task.setContacts(userId);
        String number = getNumber(task);
        task.setNumber(number);
        super.updateById(task);
        ThreadUtil.execute(() -> {
            //todo 发送通知消息
            SendMessageParam msgParam = new SendMessageParam();
            msgParam.setMethod(IMessageService.METHOD_SMS);
            msgParam.setKey("assignNotify");
            msgParam.putData("name", task.getTp().getName(), 20);
            msgParam.putData("time", DateUtil.format(task.getApplyTime(), "yyyy-MM-dd HH:mm"));
            SysUser user = this.sysUserService.getById(task.getContacts());
            msgParam.addTo(user.getPhone());
            this.messageService.send(msgParam);
            if (oldTaskContacts != null) {
                SysUser olduser = this.sysUserService.getById(oldTaskContacts);

                SendMessageParam oldmsgParam = new SendMessageParam();
                oldmsgParam.setMethod(IMessageService.METHOD_SMS);
                oldmsgParam.setKey("cancelNotify");
                oldmsgParam.putData("name", task.getTp().getName(), 20);
                oldmsgParam.putData("time", DateUtil.format(task.getApplyTime(), "yyyy-MM-dd HH:mm"));
                //通知管理員
                oldmsgParam.addTo(olduser.getPhone());
                this.messageService.send(oldmsgParam);
            }
        });
    }

    @Override
    public void assigned(Integer taskId) {

    }

    @Override
    public void againPlan(Integer taskId, LocalDateTime planTime) {
        ExamineTask task = super.getById(taskId);
        List<ExamineTaskItem> taskItems = this.examineTaskItemService.queryByTask(taskId);
        List<ExamineImg> examineImgs = examineTaskItemService.queryByTaskImg(taskId);
        List<ExamineLog> examineLogList = examineTaskItemService.queryByTaskLog(taskId);
        ExamineTask newTask = new ExamineTask();
        BeanUtil.copyProperties(task, newTask, true);
        newTask.setStatus(Constants.TASK_STATUS_WAIT);
        newTask.setContacts(null);
        newTask.setApplyTime(planTime);
        newTask.setVideoNum(null);
        newTask.setUpExamine(taskId);
        newTask.setNumber(null);
        super.save(newTask);
        taskItems.forEach(taskItem -> {
            ExamineTaskItem nitem = new ExamineTaskItem();
            BeanUtil.copyProperties(taskItem, nitem, true);
            nitem.setExamineTask(newTask.getId());
            this.examineTaskItemService.save(nitem);
        });
        examineImgs.forEach(taskImg -> {

            ExamineImg examineImg1 = new ExamineImg();
            BeanUtil.copyProperties(taskImg, examineImg1, true);
            examineImg1.setExamineTask(newTask.getId());
            examineImgService.save(examineImg1);
        });
        examineLogList.forEach(examineLog -> {
            ExamineLog examineLog1 = new ExamineLog();
            BeanUtil.copyProperties(examineLog, examineLog1, true);
            examineLog1.setExamineTask(newTask.getId());
            examineLogService.save(examineLog1);
        });
        ThreadUtil.execute(() -> {
            //发送通知短信
            SendMessageParam param = this.createSmsAgainPlanParam(newTask);
            this.messageService.send(param);


        });

    }

    @Override
    public boolean keepRoom(ExamineTask task) {
        Object obj = this.redisHelper.get(Constants.ROOM_ADMIN_FLAG + task.getId());
        //如果没有，则返回异常
        if (obj == null) {
            return false;
        } else {
            //如果商户进入，则把标记改为true
            if (obj.equals("true")) {

                return true;
            } else {
                //如果有，则刷新存储过期时间
                LocalDateTime now = LocalDateTime.now();
                this.redisHelper.set(Constants.ROOM_ADMIN_FLAG + task.getId(), now, Constants.ROOM_ADMIN_FLAG_OUTTIME);
                return false;
            }
        }
    }

    @Override
    public TaskRoomResult readRoom(Integer taskId) {
        TaskRoomResult result = null;
        Object obj = this.redisHelper.get(Constants.ROOM_ADMIN_FLAG + taskId);
        if (obj == null) {
            return null;
        }
        LocalDateTime time = (LocalDateTime) obj;
        result = new TaskRoomResult();
        result.setDate(time);
        ExamineTask task = this.getById(taskId);
        //需要判断是否已经分配，如果未分配，则返回空
        if (task.getContacts() != null) {
//            result.setRoom(task.getContacts().toString());
            result.setRoom(taskId.toString());
        } else {
            return null;
        }

        return result;
    }


    private SendMessageParam createSmsRemindRoomMessageParam(ExamineTask task) {
        SendMessageParam param = new SendMessageParam();
        //通过短信+微信的方式
        param.setMethod(IMessageService.METHOD_SMS);
        // 查询任务分类
        param.addTo(task.getShopTel());
        param.setKey("remindRoom");
        //TODO 写入参数
        if (task.getTp() != null) {
            param.putData("code", task.getShopName(), 20);
        } else {
            param.putData("code", "踏勘任务");
        }
//        param.putData("code",task.get);
//        param.putData(task.getShopName());
        return param;
    }


    /**
     * 创建二次踏勘通知短信
     *
     * @param task
     * @return
     */
    private SendMessageParam createSmsAgainPlanParam(ExamineTask task) {
        SendMessageParam param = new SendMessageParam();
        //通过短信+微信的方式
        param.setMethod(IMessageService.METHOD_SMS);
        // 查询任务分类
        param.addTo(task.getShopTel());
        param.setKey("againPlan");
        //TODO 写入参数

        String name = task.getShopName();
        String product = task.getTp().getName();
        param.putData("name", name, 20);
        param.putData("product", product, 20);
        param.putData("time", DateUtil.format(task.getApplyTime(), "yyyy-MM-dd HH:mm"));
        return param;
    }

    /**
     * 创建商户通知消息
     *
     * @param task
     * @return
     */
    private SendMessageParam createWxMessageParam(ExamineTask task) {
        SendMessageParam param = new SendMessageParam();
        //通过短信+微信的方式
        param.setMethod(IMessageService.METHOD_WX);
        // 查询任务分类
        ShopUser user = this.shopUserService.getById(task.getUser());

        param.addTo(user.getOpenid());
        param.setKey("createRoom");
        String product = task.getTp().getName();
        param.putData("thing1", product, 20);
        param.putData("time2", DateUtil.format(task.getApplyTime(), "yyyy-MM-dd HH:mm"));
        param.putData("thing3", "远程踏勘已开启，请立即进入小程序");

        return param;
    }

    @Override
    public boolean intoRoom(Integer userId, IntoTaskRoomParam param) {
        ExamineTask task = this.getById(param.getTaskId());
        //如果刷新房间访问key失败，则说明房间未开启
        Object obj = this.redisHelper.get(Constants.ROOM_ADMIN_FLAG + param.getTaskId());
        if (obj == null) {
            return false;
        }
        //写入商户加入标记
        this.redisHelper.set(Constants.ROOM_ADMIN_FLAG + task.getId(), "true", Constants.ROOM_ADMIN_FLAG_OUTTIME);

        if (!task.getUser().equals(userId)) {
            throw new IllegalParameterException("该用户不是任务发起人");
        }
        //如果房间号为空
        if (StrUtil.isEmpty(task.getVideoNum())) {
            // 使用task的ID作为房间号
            task.setVideoNum(task.getId().toString());
            task.setVideoStarttime(LocalDateTime.now());
        }
        //写入经纬度
        task.setExamineLocation(param.getLon() + "," + param.getLat());
        super.updateById(task);

        //记录商户进入房间日志
        try {
            ExamineTask examineTask = this.getById(task.getId());
            ExamineLog examineLog = new ExamineLog();
            examineLog.setCreateTime(LocalDateTime.now());
            examineLog.setShop(examineTask.getUser());
            examineLog.setStatus(ExamineLogEnum.EXAMINE_STATUS_ENUM_TWO.getValue());
            examineLog.setExamineTask(examineTask.getId());
            examineLogService.save(examineLog);
        } catch (Exception e) {

        }

        ThreadUtil.execute(() -> {
                this.startVideo(task.getId(),3);
        });

        return true;
    }

    @Override
    public boolean startVideo(Integer taskId,int flag) {
        if(flag<=0){
            return false;
        }

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("================================发起混流==============================");
        System.out.println("================================" + taskId + "==============================");
        System.out.println("================================发起混流==============================");
        StartMCUMixTranscodeRequest request = new StartMCUMixTranscodeRequest();
        try {
//            ExamineTask task=this.getById(taskId);
            request.setSdkAppId(1400451986L);
            request.setRoomId(new Long(taskId));
            OutputParams op = new OutputParams();

            op.setStreamId(taskId + "_mix");
            op.setRecordId(taskId + "_mix_file");

            op.setPureAudioStream(0L);
            op.setRecordAudioOnly(0L);
            request.setOutputParams(op);
            EncodeParams ep = new EncodeParams();
            ep.setAudioSampleRate(12000L);
            ep.setVideoHeight(1280L);
            ep.setVideoWidth(720L);
            ep.setVideoGop(2L);
            ep.setVideoBitrate(1560L);
            ep.setVideoFramerate(15L);
            ep.setAudioSampleRate(48000L);
            ep.setBackgroundColor(0L);
            ep.setAudioBitrate(64L);
            ep.setAudioChannels(2L);
            request.setEncodeParams(ep);
            LayoutParams lp = new LayoutParams();
            request.setLayoutParams(lp);
            StartMCUMixTranscodeResponse response = this.trtcClient.StartMCUMixTranscode(request);
            System.out.println("================================启动混流结果==============================");
            System.out.println(response.getRequestId());
            System.out.println("================================启动混流结果==============================");
            return true;
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
            return this.startVideo(taskId,--flag);
        }
    }

    /**
     * 中途退出
     *
     * @param task
     */
    @Override
    public void leave(ExamineTask task) {
        //删除Redis中的参数
        this.redisHelper.del(Constants.ROOM_ADMIN_FLAG + task.getId());
        this.redisHelper.set(Constants.USER_ONLINE_INFO + task.getContacts().toString(), new Boolean(false), Constants.USER_ONLINE_INFO_TIMEOUT);
        //解散直播间
        DismissRoomRequest request=new DismissRoomRequest();
        request.setRoomId(Long.valueOf(task.getId()));
        request.setSdkAppId(1400451986L);
        try {
            DismissRoomResponse response=this.trtcClient.DismissRoom(request);
            log.debug("解散房间:"+task.getId());
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }

    }

    @Override
    public synchronized boolean submit(SubmitExamineParam param) {
        ExamineTask task = this.getById(param.getTaskId());
        if (task == null) {
            throw new IllegalParameterException("请提交正确的任务信息");
        }
        ExamineTask examineTask = this.getById(param.getTaskId());
        int n = examineTaskItemService.countByType(examineTask.getTasktype(), examineTask.getUser(), examineTask.getId());
        examineTask.setOpinion(param.getRemark());
        examineTask.setOpinion(examineTask.getOpinion());
        boolean flag = false;
        //判断审核是否通过
        if (n == 0) {
            examineTask.setStatus(ExamineStatusEnum.EXAMINE_STATUS_ENUM_TWO.getValue());
            examineTask.setVideoEndtime(LocalDateTime.now());
            if (examineTask.getType().equals(TypeEnum.TYPE_ENUM_TWO.getValue())) {
                flag = this.sceneService.extract(examineTask);
            }

        } else {
            examineTask.setStatus(ExamineStatusEnum.EXAMINE_STATUS_ENUM_THREE.getValue());
            examineTask.setVideoEndtime(LocalDateTime.now());
        }
        examineTask.setActualApplyTime(examineTask.getApplyTime());
        examineTask.setApplyTime(examineTask.getVideoStarttime());
        super.updateById(examineTask);
        //发送通知消息
        this.sendApplyMessage(examineTask, flag);
        // 记录统计信息
        ThreadUtil.execute(() -> {
            Date date = new Date();
            String strDateFormat = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
            //数据分析每日新增审核数
            DataAnalysis dataAnalysis = new DataAnalysis();
            dataAnalysis.setTime(sdf.format(date));
            Wrapper<DataAnalysis> qw = new QueryWrapper<>(dataAnalysis);
            DataAnalysis dataAnalysis1 = dataAnalysisService.getOne(qw);
            DataAnalysis dataAnalysis2 = new DataAnalysis();
            if (dataAnalysis1 == null) {
                dataAnalysis2.setTime(sdf.format(date));
                dataAnalysis2.setAuditnum(1);
                dataAnalysisService.save(dataAnalysis2);
            } else {
                dataAnalysis2.setAuditnum(dataAnalysis1.getAuditnum() + 1);
                dataAnalysisService.update(dataAnalysis2, qw);
            }

            ExamineLog examineLog = new ExamineLog();
            examineLog.setCreateTime(LocalDateTime.now());
            examineLog.setShop(task.getUser());
            examineLog.setStatus(ExamineLogEnum.EXAMINE_STATUS_ENUM_FOUR.getValue());
            examineLog.setExamineTask(task.getId());
            examineLogService.save(examineLog);
        });

        return flag;
    }


    @Override
    public synchronized void liveEnd(LivePushParam param) {
        //如果是管理员的话，跳过
        if (param.getStream_id().indexOf("adminId_") != -1) {
            return;
        }
        ThreadUtil.execute(() -> {
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String[] ss = param.getStream_id().split("_");
            String taskId = ss[1];
            ExamineTask task = this.getById(taskId);

            if (task == null) {
                return;
            }

            // 从媒体资源库中获取混播视频资源
            String name1 = taskId + "_mix_file";
            String name2 = "1400451986_" + taskId + "_userId";

            Long startTime = Long.parseLong(param.getStart_time());
            VideoInfo videoInfo = this.searchFileByNamePrefixes(name1, startTime);
            if (videoInfo == null) {
                videoInfo = this.searchFileByNamePrefixes(name2, startTime);
                if (videoInfo == null) {
                    System.out.println("==================================第二次查询==================================");
                    videoInfo = new VideoInfo(param.getVideo_url(), Long.parseLong(param.getStart_time()), Long.parseLong(param.getEnd_time()), Integer.parseInt(param.getDuration()), param.getFile_id());
                }
            }
            System.out.println("==================================更新视频结果==================================");
//处理视频返回结果
            List<VideoInfo> videos = null;
            if (StrUtil.isEmpty(task.getVideoUrl())) {
                videos = new ArrayList<>();
            } else {
                videos = JSONUtil.toList(new JSONArray(task.getVideoUrl()), VideoInfo.class);
                //去除重复视频
                for (VideoInfo video : videos) {
                    if (param.getVideo_url().substring(7).equals(video.getUrl().substring(7))) {
                        return;
                    }
                }
            }
            videos.add(videoInfo);
            String videoUrl = JSONUtil.toJsonStr(videos);
            System.out.println(videoUrl);
            task.setVideoUrl(videoUrl);
            task.setVideoEndtime(LocalDateTime.now());

            super.updateById(task);
            System.out.println("==================================更新视频结果==================================");
        });
    }

    /**
     * 查询文件，并包装为需要的数据
     *
     * @param key
     * @param start
     * @return
     */
    private VideoInfo searchFileByNamePrefixes(String key, long start) {
        VideoInfo videoInfo = null;
        SearchMediaRequest req = new SearchMediaRequest();
        //查最新的
        req.setLimit(1L);
        String[] params = new String[]{key};
        req.set("NamePrefixes", params);
        boolean flag = false;
        try {
            SearchMediaResponse resp = this.client.SearchMedia(req);
            MediaInfo[] infos = resp.getMediaInfoSet();
            System.out.println("==================================视频查询:" + key + "结果:" + infos.length + "==================================");
            if (infos.length == 0) {
                return null;
            }
            MediaInfo info = infos[0];
            //info.getBasicInfo().getMediaUrl()
            System.out.println(info.getFileId() + " \t" + info.getBasicInfo().getName());

            String url = info.getBasicInfo().getMediaUrl();
            float f = info.getMetaData().getDuration();
            int duration = (int) f;
            videoInfo = new VideoInfo(url, start, start + duration, duration, info.getFileId());
            System.out.println("==================================视频查询结果==================================");
            System.out.println("========" + JSONUtil.toJsonStr(videoInfo));
            return videoInfo;
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ExamineTask assign(Integer userId, Date startTime, Date endTime) {
        //倒序读取今日的任务
        TaskQueryParam param = new TaskQueryParam();
        param.setStatus(Constants.TASK_STATUS_WAIT);
        param.setApplyStartTime(null);
        param.setApplyEndTime(endTime);
        //查询当前用户是否已经分配了任务
        param.setContacts(userId);
        Page page = new Page();
        page.setSize(1);
        List<ExamineTask> tasks = this.examineTaskMapper.queryByParam(param, page);
        if (tasks.size() > 0) {
            ExamineTask task = tasks.get(0);
            BaseTasktype type = this.tasktypeService.getById(task.getTasktype());
            if (type != null) {
                task.setTp(type);
            }
            return task;
        } else {
            //如果没有，则重新分配
            param.setContacts(-1);
            tasks = this.examineTaskMapper.queryByParam(param, page);
            if (tasks.size() > 0) {
                ExamineTask task = tasks.get(0);
                BaseTasktype type = this.tasktypeService.getById(task.getTasktype());
                if (type != null) {
                    task.setTp(type);
                }
                task.setContacts(userId);
                //todo 写入任务编号
                String number = this.getNumber(task);
                task.setNumber(number);

                super.updateById(task);
                return task;
            } else {
                return null;
            }
        }
    }

    /**
     * 任务编号
     * id 任务id
     *
     * @return
     */
    @Override
    public String getNumber(ExamineTask examineTask) {

        String number = "";
        Date date = new Date();
        String strDateFormat = "yyyyMMdd";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        number = number + sdf.format(date) + "C";
        // ExamineTask examineTask = examineTaskService.getById(id);
        String contacts = examineTask.getContacts().toString();

        if (contacts.length() == 1) {
            contacts = "00" + contacts;
        } else if (contacts.length() == 2) {
            contacts = "0" + contacts;
        } else if (contacts.length() == 3) {
            contacts = contacts;
        } else {
            contacts = contacts.substring(0, 3);
        }
        number = number + contacts + "T";

        String countByUser = String.valueOf(this.countByUser(examineTask.getContacts()) + 1);
        if (countByUser.length() == 1) {
            countByUser = "00" + countByUser;
        } else if (countByUser.length() == 2) {
            countByUser = "0" + countByUser;

        } else {
            countByUser = countByUser.substring(0, 3);
        }

        number = number + countByUser;
        return number;
    }

    @Override
    public TaskCenterResult readTaskCenter(Integer userId, Date startTime, Date endTime) {

        TaskQueryParam param1 = new TaskQueryParam();
        param1.setStatus(Constants.TASK_STATUS_WAIT);
//        param1.setApplyStartTime(startTime);
        param1.setApplyStartTime(null);
        param1.setApplyEndTime(endTime);
        //查询当前用户是否已经分配了任务
        param1.setContacts(userId);
        Page page = new Page();
        page.setSize(1);
        TaskCenterResult result = new TaskCenterResult();
        ExamineTask nowTask = null;
//        param1.setStatus(-1);
        List<ExamineTask> tasks = this.examineTaskMapper.queryByParam(param1, page);
        tasks.forEach(task -> {
            BaseTasktype type = this.tasktypeService.getById(task.getTasktype());
            if (type != null) {
                task.setTp(type);
            }
            //  ShopUser shopUser = shopUserService.getById(nowTask.getUser());
            SysUser sysUser = sysUserService.getById(task.getContacts());
            try {
                task.setContactsName(sysUser.getName());
            } catch (Exception e) {
                task.setContactsName("");
            }
        });
        result.setTasks(tasks);
        //统计数据
        TaskQueryParam param = new TaskQueryParam();
        param.setApplyStartTime(startTime);
        param.setApplyEndTime(endTime);
        param.setContacts(userId);
        List<Map<String, Object>> datas = this.examineTaskMapper.countTaskByStatus(param);
        TaskCountInfo allCount = this.parseCountInfo(datas);
        datas = this.examineTaskMapper.countTaskByStatus(param);
        TaskCountInfo myCount = this.parseCountInfo(datas);
        result.setAdoptNum(myCount.getAdopt() == null ? 0 : myCount.getAdopt());
        result.setFinishNum(allCount.getTotal() - allCount.getWait());
        result.setFailedNum(myCount.getFailed() == null ? 0 : myCount.getFailed());
        result.setMyFinishNum(myCount.getTotal() - myCount.getWait());
        result.setCount(this.countTodayNum());

//处理统计数据
        param.setApplyStartTime(null);
        // param.setContacts(-1);

        List<Map<String, Object>> datas2 = this.examineTaskMapper.countTaskByStatus(param);

        TaskCountInfo allCount2 = this.parseCountInfo(datas2);
        result.setTodoNum(allCount2.getWait() == null ? 0 : allCount2.getWait());
        return result;
    }

    @Override
    public ShopTaskCenterResult readTaskCenterByShop(Integer userId, Date startTime, Date endTime) {
        ShopTaskCenterResult result = new ShopTaskCenterResult();
        TaskQueryParam param = new TaskQueryParam();
        param.setApplyStartTime(null);
        param.setApplyEndTime(null);
        param.setUser(userId);
        List<Map<String, Object>> datas = this.examineTaskMapper.countTaskByStatus(param);
        TaskCountInfo allCount = this.parseCountInfo(datas);

        result.setTodoNum(allCount.getWait() == null ? 0 : allCount.getWait());
        result.setAdoptNum(allCount.getAdopt() == null ? 0 : allCount.getAdopt());
        result.setFinishNum(allCount.getTotal() - allCount.getWait());
        result.setFailedNum(allCount.getFailed() == null ? 0 : allCount.getFailed());
        param.setStatus(Constants.TASK_STATUS_WAIT);
        param.setPageNo(1);
        param.setPageSize(9999);
        param.setApplyEndTime(null);
        List<ExamineTask> tasks = this.queryTasks(param).getRecords();
        tasks.forEach(task -> {
            BaseTasktype type = this.tasktypeService.getById(task.getTasktype());
            if (type != null) {
                task.setTp(type);
            }
        });
        result.setTasks(tasks);
        return result;
    }

    private TaskCountInfo parseCountInfo(List<Map<String, Object>> datas) {
        TaskCountInfo result = new TaskCountInfo();
        result.setTotal(0);
        datas.forEach(m -> {
            Object numStr = m.get("n");
            Integer num = numStr == null ? 0 : Integer.parseInt(numStr.toString());
            result.setTotal(result.getTotal() + num);
            switch (m.get("s").toString()) {
                case Constants.TASK_STATUS_ERROR + "":
                    result.setFailed(num);
                    break;
                case Constants.TASK_STATUS_SUCCESS + "":
                    result.setAdopt(num);
                    break;
                case Constants.TASK_STATUS_WAIT + "":
                    result.setWait(num);
                    break;
            }
        });
        result.setFailed(result.getFailed() == null ? 0 : result.getFailed());
        result.setAdopt(result.getAdopt() == null ? 0 : result.getAdopt());
        result.setWait(result.getWait() == null ? 0 : result.getWait());
        return result;
    }

    @Override
    public Page<ExamineTask> queryTasks(TaskQueryParam param) {
        Page<ExamineTask> page = PageUtil.getPage(BeanUtil.beanToMap(param));
        List<ExamineTask> list = this.examineTaskMapper.queryByParam(param, page);
        page.setRecords(list);
        return page;
    }

    @Override
    public Page<Map<String, Object>> getList(Page page) {
        return examineTaskMapper.getList(page);
    }

    @Override
    public int countBytime(Integer tasktype, Integer timeStatus) {
        return examineTaskMapper.countBytime(tasktype, timeStatus);
    }

    @Override
    public int countByStatus(Integer tasktype, Integer status) {
        return examineTaskMapper.countBytime(tasktype, status);
    }

    @Override
    public ExamineTask getById(Serializable id) {
        ExamineTask task = super.getById(id);
        if (task != null) {
            BaseTasktype type = this.tasktypeService.getById(task.getTasktype());
            if (type != null) {
                task.setTp(type);
            }
        }
        return task;
    }

    @Override
    public Page<Map<String, Object>> getPageList(Page page, Integer tasktype, Integer shop, Integer status) {
        return examineTaskMapper.getPageList(page, tasktype, shop, status);
    }

    @Override
    public int countBytime1() {
        return examineTaskMapper.countBytime1();
    }

    @Override
    public int countApplyToday(Integer type) {
        return examineTaskMapper.countApplyToday(type);
    }

    @Override
    public Page<Map<String, Object>> examineList(Page page) {
        return examineTaskMapper.examineList(page);
    }

    @Override
    public int countByUser(Integer user) {
        return examineTaskMapper.countByUser(user);
    }

    @Override
    public void sendApplyMessage(ExamineTask task, boolean flag) {
        ThreadUtil.execute(() -> {
            SendMessageParam msgParam = this.createWxApplyMessageParam(task, flag);
            Map<String, Boolean> sendFlag = this.messageService.send(msgParam);
            //如果订阅消息推送失败，则向商户推送短信
//            if (!sendFlag.get(IMessageService.METHOD_WX)) {
            SendMessageParam smsMsgParam = this.createSmsApplyMessageParam(task, flag);
            this.messageService.send(smsMsgParam);
//            }
        });
    }

    @Override
    public int countByUserToday() {
        return examineTaskMapper.countByUserToday();
    }

    @Override
    public int countTodayNum() {
        return examineTaskMapper.countTodayNum();
    }


    /**
     * 踏勘审核结果通知
     *
     * @param task
     * @return
     */
    private SendMessageParam createSmsApplyMessageParam(ExamineTask task, boolean flag) {
        SendMessageParam param = new SendMessageParam();
        //通过短信+微信的方式
        param.setMethod(IMessageService.METHOD_SMS);
        // 查询任务分类
        param.addTo(task.getShopTel());
        if (task.getStatus() == Constants.TASK_STATUS_SUCCESS) {
            param.setKey("applyNotify");
        } else {
            param.setKey("applyNotifyFalse");
            param.putData("suggest", task.getOpinion(), 20);
        }
        // 写入参数
        if (task.getTp() != null) {

            String name = task.getTp().getName();
            param.putData("name", name, 20);
        } else {
            param.putData("name", "踏勘任务");
        }
        if (flag) {
            param.putData("msg", "远程踏勘已审核通过，但根据抽查规定，仍需进行现场踏勘，请您提前做好准备。");
        }
//        param.putData("code",task.get);
//        param.putData(task.getShopName());
        return param;
    }

    private SendMessageParam createWxApplyMessageParam(ExamineTask task, boolean flag) {
        SendMessageParam param = new SendMessageParam();
        //通过短信+微信的方式
        param.setMethod(IMessageService.METHOD_WX);
        // 查询任务分类
        ShopUser user = this.shopUserService.getById(task.getUser());
        String name = task.getTp().getName();
        param.addTo(user.getOpenid());
        param.setKey("applyNotify");
        param.putData("thing1", name, 20);
        param.putData("date2", DateUtil.format(task.getCreateTime(), "yyyy-MM-dd HH:mm"));
        param.putData("date3", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm"));
        param.putData("phrase4", task.getStatus() == Constants.TASK_STATUS_SUCCESS ? "通过" : "未通过");
        if (flag) {
            param.putData("msg", "远程踏勘已审核通过，但根据抽查规定，仍需进行现场踏勘，请您提前做好准备。");
        }

        return param;
    }


    @Override
    @Transactional
    public Map<String, Object> addTaskBySystem(ExamineTaskBySysTemVo examineTaskBySysTemVo) {
        Map<String, Object> map = new HashMap<>();
        ExamineTask examineTask = new ExamineTask();
        //任务分类
        examineTask.setTasktype(examineTaskBySysTemVo.getTasktype());
        examineTask.setShopName(examineTaskBySysTemVo.getShopName());
        examineTask.setShopNum(examineTaskBySysTemVo.getShopNum());
        examineTask.setShopTel(examineTaskBySysTemVo.getShopTel());
        examineTask.setShopAddress(examineTaskBySysTemVo.getShopAddress());
        examineTask.setUserName(examineTaskBySysTemVo.getUserName());
        examineTask.setIdCard(examineTaskBySysTemVo.getIdCard());
        examineTask.setSystemId(examineTaskBySysTemVo.getRowguid());
        examineTask.setApplyTime(examineTaskBySysTemVo.getApplyTime());
        //通过手机号查找商户信息和商户进行关联
        ShopUser shopUser = new ShopUser();
        shopUser.setPhone(examineTask.getShopTel());
        Wrapper<ShopUser> qw1 = new QueryWrapper<>(shopUser);
        ShopUser shopUser1 = shopUserService.getOne(qw1);
        if (shopUser1 == null) {
            /* return R.failed("通过此手机号，未找到商户的信息");*/
            examineTask.setUser(null);
        } else {
            examineTask.setUser(shopUser1.getId());
        }

        //如果重复，返回报错信息
        if (this.checkTask(examineTask.getUserName(), examineTask.getShopName(), examineTask.getIdCard(),  examineTask.getTasktype())) {
            map.put("code",1);
            map.put("msg","已经有同事录入了该任务，请不要重复录入");
            return map;
        }

        Date date = new Date();
        String strDateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        //数据分析每日新增申请数
        DataAnalysis dataAnalysis = new DataAnalysis();
        dataAnalysis.setTime(sdf.format(date));
        Wrapper<DataAnalysis> qw = new QueryWrapper<>(dataAnalysis);
        try {
            DataAnalysis dataAnalysis1 = dataAnalysisService.getOne(qw);
            DataAnalysis dataAnalysis2 = new DataAnalysis();
            if (dataAnalysis1 == null) {
                dataAnalysis2.setTime(sdf.format(date));
                dataAnalysis2.setApplynum(1);
                dataAnalysisService.save(dataAnalysis2);
            } else {
                dataAnalysis2.setApplynum(dataAnalysis1.getApplynum() + 1);
                dataAnalysisService.update(dataAnalysis2, qw);
            }
        } catch (Exception e) {

        }
        //新增申请
        examineTask.setStatus(ExamineStatusEnum.EXAMINE_STATUS_ENUM_ONE.getValue());
        examineTask.setCreateTime(LocalDateTime.now());
        examineTask.setUpdateTime(LocalDateTime.now());
        examineTask.setType(TypeEnum.TYPE_ENUM_TWO.getValue());
        boolean flag = examineTaskService.save(examineTask);
        if(flag){
            //TODO 前置机更改状态 此记录已经插入，管理表的数据
            isMainService.edit(examineTask.getSystemId());
            SendMessageParam msgParam = this.createSmsMessageParam(examineTask);
            ThreadUtil.execute(() -> {
                this.messageService.send(msgParam);
            });
            map.put("code",2);
            map.put("msg","成功");
            return map;
        }
        return map;
    }

    @Override
    @Transactional
    public void sysncData(ExamineTask examineTask) {
        //探勘完成同步数据
        isMainService.insert(examineTask.getSystemId(),examineTask.getStatus(),examineTask.getOpinion());
       //更新是否已经被同步了的状态
        examineTask.setState(2);
        examineTaskService.updateById(examineTask);
    }

    /**
     * 检查同一个人同时只有一个踏勘任务
     *
     * @param name
     * @param shopName
     * @param code
     * @return
     */
    private boolean checkTask(String name, String shopName, String code, Integer taskType) {
        ExamineTask param = new ExamineTask();
        param.setUserName(name);
        param.setShopName(shopName);
//        param.setShopTel(phone);
//        param.setIdCard(code);
//        param.setTasktype(taskType);
        try {
            ExamineTask task = this.examineTaskService.getOne(new QueryWrapper<>(param));
            if (task != null) {
                return true;
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    /**
     * 创建商户通知消息
     *
     * @param task
     * @return
     */
    private SendMessageParam createSmsMessageParam(ExamineTask task) {
        SendMessageParam param = new SendMessageParam();
        //通过短信+微信的方式
        param.setMethod(IMessageService.METHOD_SMS);
        // 查询任务分类
        param.addTo(task.getShopTel());
        param.setKey("createRoom");
        String name = task.getUserName();
        param.putData("code", name, 20);
//        param.putData("code",task.get);
//        param.putData(task.getShopName());
        return param;
    }

}
