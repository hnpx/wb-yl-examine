package com.px.pa.modulars.examine.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zhouz
 */
@ApiModel("踏勘视频回收站")
@Data
public class ExamineVideoParam {
    @ApiModelProperty("踏勘状态")
    private Integer status;
    @ApiModelProperty("删除人")
    private Integer delUser;
    @ApiModelProperty("任务ID")
    private Integer taskId;
    @ApiModelProperty("时间之前")
    private Date  lastTime;
    @ApiModelProperty("文件ID")
    private String fileId;
    @ApiModelProperty("备注")
    private String remark;
}
