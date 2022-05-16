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

package com.px.pa.modulars.examine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.px.pa.modulars.examine.entity.ExamineTask;
import com.px.pa.modulars.examine.entity.ExamineTaskScene;
import com.px.pa.modulars.examine.vo.SceneAssignParam;
import com.px.pa.modulars.examine.vo.SceneResultParam;

/**
 * 现场踏勘任务
 *
 * @author 吕郭飞
 * @date 2020-12-17 10:54:47
 */
public interface ExamineTaskSceneService extends IService<ExamineTaskScene> {
    /**
     * 抽取现场踏勘任务
     *
     * @param task
     * @return
     */
    public boolean extract(ExamineTask task);

    /**
     * 指派现场踏勘任务
     *
     * @param param
     */
    public void assign(SceneAssignParam param);


    /**
     * 指派现场踏勘任务分任务给业务员
     *
     * @param
     */
    public void assignExamineTask(SceneAssignParam param);

    /**
     * 提交踏勘结果
     *
     * @param param
     */
    public void submit(SceneResultParam param,Integer userId);

}
