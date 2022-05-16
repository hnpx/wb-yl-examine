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
import com.fasterxml.jackson.annotation.JsonFormat;
import com.px.pa.modulars.base.entity.BaseTasktype;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 任务信息
 *
 * @author lp
 * @date 2020-11-26 14:27:43
 */
@Data
@TableName("examine_task")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "任务信息")
public class ExamineTask extends Model<ExamineTask> {
    private static final long serialVersionUID = 1L;

    /**
     * 申请人
     */
    @ApiModelProperty(value = "申请人")
    private Integer user;
    @ApiModelProperty("用户姓名")
    private String userName;

   /* @ApiModelProperty("用户姓名")
    private String userName;*/
    /**
     * 预约时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "预约时间")
    private LocalDateTime applyTime;
    /**
     * 店铺名称
     */
    @ApiModelProperty(value = "店铺名称")
    private String shopName;
    /**
     * 店铺地址
     */
    @ApiModelProperty(value = "店铺地址")
    private String shopAddress;
    /**
     * 店铺定位
     */
    @ApiModelProperty(value = "店铺定位")
    private String shopLocation;
    /**
     * 店铺联系电话
     */
    @ApiModelProperty(value = "店铺联系电话")
    private String shopTel;
    /**
     * 紧急联系人
     */
    @ApiModelProperty(value = "紧急联系人")
    private String otherUser;
    /**
     * 紧急联系电话
     */
    @ApiModelProperty(value = "紧急联系电话")
    private String otherTel;
    /**
     * 统一社会信用代码
     */
    @ApiModelProperty(value = "统一社会信用代码")
    private String shopNum;
    /**
     * 实际预约时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "实际预约时间")
    private LocalDateTime actualApplyTime;
    /**
     * 上一次审核
     */
    @ApiModelProperty(value = "上一次审核")
    private Integer upExamine;
    /**
     * 审核员
     */
    @ApiModelProperty(value = "审核员")
    private Integer contacts;
    /**
     * 视频房间号
     */
    @ApiModelProperty(value = "视频房间号")
    private String videoNum;
    /**
     * 视频开始时间
     */

    @ApiModelProperty(value = "视频开始时间")
    private LocalDateTime videoStarttime;
    /**
     * 视频结束时间
     */
    @ApiModelProperty(value = "视频结束时间")
    private LocalDateTime videoEndtime;
    /**
     * 视频地址
     */
    @ApiModelProperty(value = "视频地址")
    private String videoUrl;
    /**
     * 踏勘时定位
     */
    @ApiModelProperty(value = "踏勘时定位")
    private String examineLocation;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private Integer createBy;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private Integer updateBy;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
    /**
     * 删除标记
     */
    @ApiModelProperty(value = "删除标记")
    private Integer enable;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;
    @ApiModelProperty(value = "任务分类")
    private Integer tasktype;
    @TableField(exist = false)
    private BaseTasktype tp;
    @ApiModelProperty(value = "身份证号")
    private String idCard;
    @ApiModelProperty(value = "状态")
    private Integer status;
    @ApiModelProperty(value = "审核意见")
    private String opinion;

    @ApiModelProperty(value = "任务编号")
    private String number;

    @ApiModelProperty(value = "评价")
    private Integer evaluation;

    /**
     * id
     */
    @TableId
    @ApiModelProperty(value = "id")
    private Integer id;
    @TableField(exist = false)
    private String tasktypeName;
    @TableField(exist = false)
    private String contactsName;
    @ApiModelProperty(value = "踏勘类型")
    private Integer type;
    @ApiModelProperty(value = "前置机管理表")
    private String systemId;
    @ApiModelProperty(value = "是否更新到了前置机（1.未更新2.已更新）")
    private Integer state;

    @TableField(exist = false)
    public String startTime;
    @TableField(exist = false)
    public String endTime;

}
