package com.px.pa.modulars.examine.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.px.pa.modulars.examine.entity.ExamineTask;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 任务中心
 * @author zhouz
 */
@ApiModel("任务中心")
@Data
@ToString
public class TaskCenterResult {
    @ApiModelProperty("待完成数量")
    private Integer todoNum;
    @ApiModelProperty("我的完成数量")
    private Integer myFinishNum;
    @ApiModelProperty("完成数量")
    private Integer finishNum;
    @ApiModelProperty("通过数量")
    private Integer failedNum;
    @ApiModelProperty("未通过数量")
    private Integer adoptNum;
    @ApiModelProperty("当前任务，如果为空，则表示没有新的任务了")
    private List<ExamineTask> tasks;
    @ApiModelProperty("未分配的活动数")
    private Integer count;

}
