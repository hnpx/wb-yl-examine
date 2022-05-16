package com.px.pa.modulars.upms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * 用户信息列表
 */
@Data
@ApiModel("用户信息列表")
public class UserInfoListResult {
    @ApiModelProperty("ID")
    private Integer id;
    @ApiModelProperty("姓名")
    private String name;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("是否在线")
    private Boolean online;
    @ApiModelProperty("是否正在踏勘")
    private Boolean task;
}
