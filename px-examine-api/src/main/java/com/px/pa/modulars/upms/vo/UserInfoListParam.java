package com.px.pa.modulars.upms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zhouz
 */
@Data
@ApiModel("用户信息列表查询")
public class UserInfoListParam {
    @ApiModelProperty("开始时间")
    private Date start;
    @ApiModelProperty("结束时间")
    private Date end;
    @ApiModelProperty("在线状态")
    private Integer online;
    @ApiModelProperty("任务状态")
    private Integer task;
}
