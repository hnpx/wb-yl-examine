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
package com.px.pa.modulars.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.px.pa.modulars.base.entity.WxConfig;
import com.px.pa.modulars.base.mapper.WxConfigMapper;
import com.px.pa.modulars.base.service.WxConfigService;
import org.springframework.stereotype.Service;

/**
 * 微信配置
 *
 * @author lp
 * @date 2020-11-30 14:54:24
 */
@Service
public class WxConfigServiceImpl extends ServiceImpl<WxConfigMapper, WxConfig> implements WxConfigService {

}
