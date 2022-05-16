package com.px.pa.modulars.examine.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PercentageVo {
    @ApiModelProperty("分类名字")
    private String tasktype;
    @ApiModelProperty("分类任务数量")
    private int count;
    @ApiModelProperty("分类任务通过数量")
    private int pass;
    @ApiModelProperty("分类任务未通过数量")
    private int noPass;
    @ApiModelProperty("申请数")
    private int apply;
    @ApiModelProperty("任务总数")
    private int countSum;
    @ApiModelProperty("进行数")
    private int run;

    private String date;
}
