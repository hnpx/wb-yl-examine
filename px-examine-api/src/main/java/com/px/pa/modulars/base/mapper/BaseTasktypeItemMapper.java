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

package com.px.pa.modulars.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.px.pa.modulars.base.entity.BaseTasktypeItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 踏勘项目
 *
 * @author lp
 * @date 2020-11-26 14:30:25
 */
@Mapper
public interface BaseTasktypeItemMapper extends BaseMapper<BaseTasktypeItem> {

    List<BaseTasktypeItem> getList(@Param("tasktype") Integer tasktype);
}
