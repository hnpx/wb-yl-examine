package com.px.pa.modulars.examine.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhouz
 */
@ApiModel("审核结果提交")
@Data
public class SubmitExamineParam {
    @ApiModelProperty("审核结果")
    private Integer status;
    @ApiModelProperty("审核备注")
    private String remark;
    @ApiModelProperty("任务ID")
    private Integer taskId;

}
