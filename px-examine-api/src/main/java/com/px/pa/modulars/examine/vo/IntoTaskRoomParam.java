package com.px.pa.modulars.examine.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 *
 */
@ApiModel("进入房间的时候参数")
@Data
@ToString
public class IntoTaskRoomParam {
    @ApiModelProperty("经度")
    private String lon;
    @ApiModelProperty("纬度")
    private String lat;
    @ApiModelProperty("任务ID")
    private Integer taskId;
}
