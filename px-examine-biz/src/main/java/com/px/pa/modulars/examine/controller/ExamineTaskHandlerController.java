package com.px.pa.modulars.examine.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.Inner;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.px.pa.constants.ExamineLogEnum;
import com.px.pa.modulars.auth.service.RemoteTokenServiceImpl;
import com.px.pa.modulars.base.entity.BaseTasktype;
import com.px.pa.modulars.base.service.BaseTasktypeService;
import com.px.pa.modulars.examine.entity.ExamineLog;
import com.px.pa.modulars.examine.entity.ExamineTask;
import com.px.pa.modulars.examine.service.ExamineLogService;
import com.px.pa.modulars.examine.service.ExamineTaskService;
import com.px.pa.modulars.examine.service.ExamineVideoBinService;
import com.px.pa.modulars.examine.vo.*;
import com.px.pa.modulars.video.vo.MixedioVo;
import com.tencentcloudapi.common.JsonResponseModel;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.trtc.v20190722.models.StartMCUMixTranscodeRequest;
import com.tencentcloudapi.trtc.v20190722.models.StartMCUMixTranscodeResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.checkerframework.common.value.qual.IntRangeFromNonNegative;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 视频踏勘任务操作
 */
@RestController
@RequestMapping("/examine/task/handler")
@Api("远程踏勘任务操作")
public class
ExamineTaskHandlerController {
    @Autowired
    private ExamineTaskService taskService;
    @Autowired
    private RemoteTokenServiceImpl tokenService;
    @Autowired
    private ExamineLogService examineLogService;
    @Autowired
    private BaseTasktypeService baseTasktypeService;
    @Autowired
    private ExamineVideoBinService videoBinService;
    @Autowired
    private ExamineTaskService examineTaskService;
    @Value("${video-file.close-time}")
    private Integer closeTime;

    @GetMapping("/assign")
    @ApiOperation("主动认领任务")
    public Object assignContacts() {
        Integer userId = SecurityUtils.getUser().getId();
        Date now = new Date();
        ExamineTask task = this.taskService.assign(userId, DateUtil.beginOfDay(now), DateUtil.endOfDay(now));
        return R.ok(task);
    }

    @GetMapping("/read/center")
    @ApiOperation("查看任务中心")
    public Object readCenter() {
        Integer userId = SecurityUtils.getUser().getId();
        Date now = new Date();
        TaskCenterResult result = this.taskService.readTaskCenter(userId, DateUtil.beginOfDay(now), DateUtil.endOfDay(now));
        return R.ok(result);
    }

    @PutMapping("/read/tasks/my")
    @ApiOperation("查询我踏勘的任务")
    public Object queryMyTasks(TaskQueryParam param) {
        param.setContacts(SecurityUtils.getUser().getId());
        if (param.getStatus() == null) {
            param.setStatus(-1);
        }
        Page<ExamineTask> taskPage = this.taskService.queryTasks(param);
        taskPage.getRecords().forEach(a -> {
            BaseTasktype baseTasktype = baseTasktypeService.getById(a.getTasktype());
            try {
                a.setTasktypeName(baseTasktype.getName());
            } catch (Exception e) {
                a.setTasktypeName("");
            }
        });
        return R.ok(taskPage);
    }


    @PostMapping("/againPlan")
    @ApiOperation("二次踏勘")
    public Object againPlan(@RequestBody AgainPlanParam param) {
        this.taskService.againPlan(param.getTaskId(), param.getTime());
        return R.ok();
    }

    @PutMapping("/read/tasks")
    @ApiOperation("查询我踏勘的任务")
    public Object queryTasks(@RequestBody TaskQueryParam param) {
        Page<ExamineTask> taskPage = this.taskService.queryTasks(param);
        return R.ok(taskPage);
    }

    @GetMapping("/open/{taskId}")
    @ApiOperation("管理员开启房间")
    public Object openRoom(@PathVariable("taskId") Integer taskId) {

        ExamineTask task = this.taskService.getById(taskId);
        ExamineLog examineLog = new ExamineLog();
        examineLog.setCreateTime(LocalDateTime.now());
        examineLog.setShop(task.getUser());
        examineLog.setStatus(ExamineLogEnum.EXAMINE_STATUS_ENUM_ONE.getValue());
        examineLog.setExamineTask(task.getId());
        examineLogService.save(examineLog);
        if (task == null) {
            return R.failed("请输入正确的任务编号");
        }
        this.taskService.openRoom(task);
        return R.ok();
    }

    @GetMapping("/keep/{taskId}")
    @ApiOperation("管理员保持房间链接")
    public Object keepRoom(@PathVariable("taskId") Integer taskId) {
        ExamineTask task = this.taskService.getById(taskId);
        if (task == null) {
            return R.failed("请输入正确的任务编号");
        }
        boolean flag = this.taskService.keepRoom(task);
        return R.ok(flag);
    }

    @GetMapping("/leave/{taskId}")
    @ApiOperation(value = "管理员退出房间", notes = "管理员退出房间后清除房间信息，防止商户进入")
    public Object leave(@PathVariable("taskId") Integer taskId) {
        ExamineTask task = this.taskService.getById(taskId);
        ExamineLog examineLog = new ExamineLog();
        examineLog.setCreateTime(LocalDateTime.now());
        examineLog.setShop(task.getUser());
        examineLog.setStatus(ExamineLogEnum.EXAMINE_STATUS_ENUM_THREE.getValue());
        examineLog.setExamineTask(task.getId());
        examineLogService.save(examineLog);
        if (task == null) {
            return R.failed("请输入正确的任务编号");
        }
        this.taskService.leave(task);
        return R.ok();
    }

    @Scheduled(cron = "0 0/1 * ? * *")
    public void dismissRoom(){
        List<ExamineTask> list=taskService
                .lambdaQuery()
                .lt(ExamineTask::getVideoStarttime,LocalDateTime.now().plusSeconds(-closeTime*60L))
                .isNull(ExamineTask::getVideoEndtime)
                .list();
        for (ExamineTask task:list){
            try{
                taskService.leave(task);
                task.setVideoEndtime(LocalDateTime.now());
                taskService.saveOrUpdate(task);
            }catch (Exception e){

            }
        }
    }


    /**
     * 退出房间
     * TODO 1、如果未开始，则直接退出
     * TODO 2、如果已经开始，房间中有一个人，则退出后保留房间
     * TODO 3、如果已经开始，房间中仅自己，则退出后结束本次踏勘（如果再次进入是否重新开启踏勘？视频踏勘可能存在多个视频文件）
     */
    @GetMapping("/end/{taskId}")
    public Object liveEnd(@PathVariable("taskId") Integer taskId) {
        return R.ok();
    }

    @PostMapping("/submit")
    @ApiOperation("小程序提交审核结果")
    public Object submit(@RequestBody SubmitExamineParam param) {

        return R.ok();
    }

    /**
     * 查询是否已经上传了视频
     * TODO 1、已经回调，则返回视频地址列表
     * TODO 2、未回调，则显示未获取到视频列表
     */


    @PostMapping("/dis/file")
    @ApiOperation("删除视频文件")
    public Object disVideoFile(ExamineVideoParam param) {
        this.videoBinService.disFile(param.getTaskId(), param.getFileId(), SecurityUtils.getUser().getId(), param.getRemark());
        return R.ok();
    }

    @PostMapping("/video/mixedio")
    @ApiOperation("视频混流开启")
    @Inner(value = false)
    public Object mixedio(Integer taskId) {
        Boolean b = examineTaskService.startVideo(taskId, 3);
        return R.ok(b);
    }


}
