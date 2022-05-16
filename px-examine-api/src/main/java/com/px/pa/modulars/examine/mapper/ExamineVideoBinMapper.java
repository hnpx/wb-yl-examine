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

package com.px.pa.modulars.examine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.px.pa.modulars.examine.entity.ExamineVideoBin;
import com.px.pa.modulars.examine.vo.ExamineVideoParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 踏勘视频回收记录
 *
 * @author 吕郭飞
 * @date 2020-12-16 11:51:15
 */
@Mapper
public interface ExamineVideoBinMapper extends BaseMapper<ExamineVideoBin> {
    /**
     * 查询视频踏勘文件
     *
     * @param param
     * @return
     */
    public List<ExamineVideoBin> queryByParam(@Param("param") ExamineVideoParam param);
}
