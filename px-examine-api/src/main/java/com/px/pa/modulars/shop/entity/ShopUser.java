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

package com.px.pa.modulars.shop.entity;

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
 * 商户信息
 *
 * @author lp
 * @date 2020-11-26 14:32:18
 */
@Data
@TableName("shop_user")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "商户信息")
public class ShopUser extends Model<ShopUser> {
private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    @ApiModelProperty(value="姓名")
    private String name;
    /**
     * 手机号
     */
    @ApiModelProperty(value="手机号")
    private String phone;
    @ApiModelProperty("身份证号")
    private String idCard;
    /**
     * OpenId
     */
    @ApiModelProperty(value="OpenId")
    private String openid;
    /**
     * 最后登录时间
     */
    @ApiModelProperty(value="最后登录时间")
    private LocalDateTime lastLogintime;
    /**
     * 头像
     */
    @ApiModelProperty(value="头像")
    private String photo;
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
     * id
     */
    @TableId
    @ApiModelProperty(value="id")
    private Integer id;
    @ApiModelProperty(value = "昵称")
    private String nickname;

    /**
     * 手机验证码
     */
    @TableField(exist = false)
    private String codePhone;

    @TableField(exist = false)
    private String code;
    @TableField(exist = false)
    private String encryptedData;
    @TableField(exist = false)
    private String iv;
    @TableField(exist = false)
    private String avatarUrl;


    }
