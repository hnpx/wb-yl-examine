package com.px.pa.modulars.examine.vo;

import com.px.pa.modulars.examine.entity.ExamineTask;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 任务中心
 *
 * @author zhouz
 */
@ApiModel("商户的任务中心")
@Data
@ToString
public class ShopTaskCenterResult {
    @ApiModelProperty("待完成数量")
    private Integer todoNum;
    @ApiModelProperty("完成数量")
    private Integer finishNum;
    @ApiModelProperty("通过数量")
    private Integer failedNum;
    @ApiModelProperty("未通过数量")
    private Integer adoptNum;
    @ApiModelProperty("当前任务列表")
    private List<ExamineTask> tasks;

}
