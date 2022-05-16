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
package com.px.pa.modulars.examine.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.px.pa.modulars.base.entity.DataAnalysis;
import com.px.pa.modulars.examine.entity.ExamineImg;
import com.px.pa.modulars.examine.mapper.ExamineImgMapper;
import com.px.pa.modulars.examine.service.ExamineImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 踏勘截图
 *
 * @author lp
 * @date 2020-12-08 10:39:51
 */
@Service
public class ExamineImgServiceImpl extends ServiceImpl<ExamineImgMapper, ExamineImg> implements ExamineImgService {

    @Resource
    private ExamineImgMapper examineImgMapper;
    @Override
    public ExamineImg getExamineImg(Integer shop,Integer examineTask) {
        return examineImgMapper.getExamineImg(shop,examineTask);
    }
}
