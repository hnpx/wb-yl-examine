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

package com.px.pa.modulars.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.px.pa.modulars.shop.entity.ShopUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商户信息
 *
 * @author lp
 * @date 2020-11-26 14:32:18
 */
@Mapper
public interface ShopUserMapper extends BaseMapper<ShopUser> {

}
