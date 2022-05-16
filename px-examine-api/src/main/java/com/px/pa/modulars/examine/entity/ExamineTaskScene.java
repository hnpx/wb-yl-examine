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

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 现场踏勘任务
 *
 * @author 吕郭飞
 * @date 2020-12-17 10:54:47
 */
@Data
@TableName("examine_task_scene")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "现场踏勘任务")
public class ExamineTaskScene extends Model<ExamineTaskScene> {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    @ApiModelProperty(value = "")
    private Integer id;
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

    @ApiModelProperty(value = "审核员姓名")
    @TableField(exist = false)
    private String contactsName;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    /**
     * 备注信息
     */
    @ApiModelProperty(value = "备注信息")
    private String remark;
    /**
     * 视频记录
     */
    @ApiModelProperty(value = "视频记录")
    private String videoUrls;
    /**
     * 图片记录
     */
    @ApiModelProperty(value = "图片记录")
    private String imgUrls;
    /**
     * 状态：1、未开始；2、通过；3、未通过
     */
    @ApiModelProperty(value = "状态：1、未开始；2、通过；3、未通过")
    private Integer status;
    /**
     * 所属的远程踏勘
     */
    @ApiModelProperty(value = "所属的远程踏勘")
    private Integer examineTask;

    @ApiModelProperty(value = "踏勘结果提交时间")
    private Date updateTime;

    @ApiModelProperty(value = "更新人")
    private Integer updateBy;
    @ApiModelProperty(value = "删除标记")
    private Integer enable;

    @ApiModelProperty("踏勘任务")
    @TableField(exist = false)
    private ExamineTask task;



}
