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

import java.time.LocalDateTime;

/**
 * 任务审核
 *
 * @author lp
 * @date 2020-11-26 14:26:34
 */
@Data
@TableName("examine_task_item")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "任务审核")
public class ExamineTaskItem extends Model<ExamineTaskItem> {
private static final long serialVersionUID = 1L;

    /**
     * 任务分类
     */
    @ApiModelProperty(value="任务分类")
    private Integer tasktype;
    /**
     * 审批项
     */
    @ApiModelProperty(value="审批项")
    private Integer tasktypeItem;
    /**
     * 1：通过；2：未通过
     */
    @ApiModelProperty(value="1：通过；2：未通过")
    private Integer status;
    /**
     * 创建人
     */
    @ApiModelProperty(value="创建人")
    private Integer createBy;
    /**
     * 创建时间
     */
    @ApiModelProperty(value="创建时间")
    private LocalDateTime createTime;
    /**
     * 更新人
     */
    @ApiModelProperty(value="更新人")
    private Integer updateBy;
    /**
     * 更新时间
     */
    @ApiModelProperty(value="更新时间")
    private LocalDateTime updateTime;
    /**
     * 删除标记
     */
    @ApiModelProperty(value="删除标记")
    private Integer enable;
    /**
     * 备注
     */
    @ApiModelProperty(value="备注")
    private String remark;
    /**
     * 排序
     */
    @ApiModelProperty(value="排序")
    private Integer sort;
    @ApiModelProperty(value="商铺id")
    private Integer shop;
    /**
     * id
     */
    @TableId
    @ApiModelProperty(value="id")
    private Integer id;

    @TableField(exist =false)
    private String img;
    @ApiModelProperty(value = "一级标题")
    private String totle;
    @ApiModelProperty(value = "任务id")
    private Integer examineTask;
    @TableField(exist = false)
    private String info;
    }
