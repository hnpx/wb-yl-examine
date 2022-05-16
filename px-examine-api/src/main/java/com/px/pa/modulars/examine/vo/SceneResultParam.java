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
@ApiModel("现场踏勘结果登记")
public class SceneResultParam {
    @ApiModelProperty(value = "现场踏勘ID")
    private Integer id;
    @ApiModelProperty(value = "现场踏勘ID")
    private String imgUrls;
    @ApiModelProperty(value = "踏勘视频")
    private String videoUrls;
    @ApiModelProperty(value = "踏勘备注")
    private String remark;
    @ApiModelProperty(value = "踏勘结果")
    private Integer status;
}
