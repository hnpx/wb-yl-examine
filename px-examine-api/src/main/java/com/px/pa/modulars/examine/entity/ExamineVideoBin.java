/*
 *    Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the pig4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: lengleng (wangiegie@gmail.com)
 */

package com.px.pa.modulars.examine.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 踏勘视频回收记录
 *
 * @author 吕郭飞
 * @date 2020-12-16 11:51:15
 */
@Data
@TableName("examine_video_bin")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "踏勘视频回收记录")
public class ExamineVideoBin extends Model<ExamineVideoBin> {
private static final long serialVersionUID = 1L;

    /**
     * 
     */
    @TableId
    @ApiModelProperty(value="")
    private Integer id;
    /**
     * 回收时间
     */
    @ApiModelProperty(value="回收时间")
    private LocalDateTime disTime;
    /**
     * 删除人
     */
    @ApiModelProperty(value="删除人")
    private Integer delUser;
    /**
     * 状态：1、回收状态；2、删除状态
     */
    @ApiModelProperty(value="状态：1、回收状态；2、删除状态")
    private Integer status;
    /**
     * 删除时间
     */
    @ApiModelProperty(value="删除时间")
    private LocalDateTime delTime;
    /**
     * 删除原因
     */
    @ApiModelProperty(value="删除原因")
    private String remark;
    /**
     * 所属任务ID
     */
    @ApiModelProperty(value="所属任务ID")
    private Integer taskId;
    /**
     * 文件ID
     */
    @ApiModelProperty(value="文件ID")
    private String fileId;
    /**
     * 开始时间
     */
    @ApiModelProperty(value="开始时间")
    private String startTime;
    /**
     * 删除时间
     */
    @ApiModelProperty(value="删除时间")
    private String endTime;
    /**
     * 时长
     */
    @ApiModelProperty(value="时长")
    private Integer duration;
    /**
     * 视频地址
     */
    @ApiModelProperty(value="视频地址")
    private String videoUrl;
    }
