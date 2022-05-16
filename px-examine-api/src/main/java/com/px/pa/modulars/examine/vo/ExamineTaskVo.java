package com.px.pa.modulars.examine.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamineTaskVo {
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "评审员")
    private Integer contacts;
    @ApiModelProperty(value = "视频开始时间")
    private LocalDateTime videoStarttime;
    @ApiModelProperty(value = "任务分类id")
    private Integer tasktype;
    @ApiModelProperty(value = "商铺名称")
    private String shopName;
    @ApiModelProperty(value = "商铺地址")
    private String shopAddress;
    @ApiModelProperty(value = "状态")
    private Integer status;

    private String contactsName;

    private String tasktypeName;

    private String phone;
    private String opinion;
    private LocalDateTime applyTime;
    private Integer evaluation;

}
