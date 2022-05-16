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
import com.px.pa.modulars.examine.entity.ExamineVideoBin;

/**
 * 踏勘视频回收记录
 *
 * @author 吕郭飞
 * @date 2020-12-16 11:51:15
 */
public interface ExamineVideoBinService extends IService<ExamineVideoBin> {
    public void delOverdueFiles();

    /**
     * 废弃任务中的某个视频文件
     *
     * @param taskId
     * @param fileId
     * @param userId 当前用户
     * @param remark
     */
    public void disFile(Integer taskId, String fileId, Integer userId, String remark);

    /**
     * 还原
     *
     * @param taskId
     * @param fileId
     */
    public void reduction(Integer taskId, String fileId);
}
