package com.px.pa.modulars.examine.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务房间
 */
@Data
public class TaskRoomResult {
    @ApiModelProperty("管理员ID，如果是房间模式，则返回房间编号")
    private String room;
    @ApiModelProperty("管理员开启的时间")
    private LocalDateTime date;

}
