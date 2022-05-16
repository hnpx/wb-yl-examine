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

package com.px.pa.modulars.base.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 行政事务
 *
 * @author lp
 * @date 2020-11-26 14:31:19
 */
@Data
@TableName("base_matter")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "行政事务")
public class BaseMatter extends Model<BaseMatter> {
private static final long serialVersionUID = 1L;

    /**
     * 事务名称
     */

    @ApiModelProperty(value="事务名称")
    private String name;
    /**
     * 责任部门
     */
    @ApiModelProperty(value="责任部门")
    private String dept;
    /**
     * 办理说明
     */
    @ApiModelProperty(value="办理说明")
    private String info;
    /**
     * 所需申请材料
     */
    @ApiModelProperty(value="所需申请材料")
    private String information;
    /**
     * 咨询电话
     */
    @ApiModelProperty(value="咨询电话")
    private String tel;
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
    /**
     * logo
     */
    @ApiModelProperty(value="logo")
    private String logo;
    @ApiModelProperty(value = "办理时长")
    private String duration;
    /**
     * id
     */
    @TableId
    @ApiModelProperty(value="id")
    private Integer id;
    }
