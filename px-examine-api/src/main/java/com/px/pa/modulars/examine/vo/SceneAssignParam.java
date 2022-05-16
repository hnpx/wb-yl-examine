package com.px.pa.modulars.examine.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 指派现场踏勘任务
 *
 * @author zhouz
 */
@Data
@ToString
@ApiModel("现场踏勘任务指派")
public class SceneAssignParam {

    /**
     * 审核时间
     */
    @ApiModelProperty(value = "审核时间")
    private LocalDateTime applyTime;
    /**
     * 审核员
     */
    @ApiModelProperty(value = "审核员")
    private Integer contacts;

    @ApiModelProperty(value = "远程踏勘ID")
    private Integer id;
}
